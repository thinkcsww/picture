package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.RestTemplateInterceptor;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

import static com.applory.pictureserver.TestConstants.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChattingControllerTest {

    @LocalServerPort
    private Integer port;

    BlockingQueue<ChattingDto.SendMessageParams> blockingQueue;
    WebSocketStompClient stompClient;
    StompSession stompSession;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private ChattingRoomMemberRepository chattingRoomMemberRepository;

    @Autowired
    private ChattingMessageRepository chattingMessageRepository;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        clearInterceptors();
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    public void cleanUp() {
        requestRepository.deleteAll();
        chattingMessageRepository.deleteAll();
        chattingRoomMemberRepository.deleteAll();
        chattingRoomRepository.deleteAll();
        userRepository.deleteAll();

        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    @Test
    public void startChatting_withInvalidToken_stompSessionIsNull() {

        try {
            connectStomp(null);
        } catch (Exception e) {
        }

        assertThat(stompSession).isNull();
    }

    @Test
    public void startChatting_withValidToken_stompSessionIsNotNull() throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {

        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        assertThat(stompSession).isNotNull();
    }

    @Disabled
    @Test
    public void getRooms_withInvalidToken_receive401() {
        authenticate("asda");
        ResponseEntity<Object> response = getRooms(0, 5, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receive200() {
        signUp(TestUtil.createValidClientUser(TEST_SELLER_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<Object> response = getRooms(0, 5, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receivePagedRoomVmList() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});

        assertThat(roomsResponse.getBody().get(0).getId()).isEqualTo(roomInfo.getRoomId());
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receiveRoomsOrderByLatestTime() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME + "2");
        UserDto.Create user3 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME + "3");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();
        UserDto.VM receiver2 = signUp(user3, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessageParams createMessage = new ChattingDto.SendMessageParams();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingDto.SendMessageParams createMessage2 = new ChattingDto.SendMessageParams();
        createMessage2.setMessage("HI");
        createMessage2.setUserIdList(Arrays.asList(sender.getId(), receiver2.getId()));
        createMessage2.setSenderId(sender.getId());
        createMessage2.setRoomId(UUID.randomUUID());

        sendMessage(createMessage2);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});

        assertThat(roomsResponse.getBody().get(0).getLastMessageDt()).isAfter(roomsResponse.getBody().get(1).getLastMessageDt());
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receiveRoomWithLastMessageAndSentTime() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});

        assertThat(roomsResponse.getBody().get(0).getLastMessage()).isEqualTo("HI2");
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receiveRoomWithUnreadCountWhenIamSender() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getUnreadCount()).isEqualTo(0);
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receiveRoomWithUnreadCountWhenIamReceiver() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME + "2"), MyOAuth2Token.class);
        clearInterceptors();
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getUnreadCount()).isEqualTo(2);
    }

    @Disabled
    @Test
    public void getRooms_withValidToken_receiveRoomWithOpponentNickname() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getOpponent()).isNotNull();
    }

    @Test
    public void enterRoom_withInvalidToken_receive401() {
        authenticate("asda");

        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.set("roomId", UUID.randomUUID().toString());
        ResponseEntity<Object> response = enterRoom(multiValueMap, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void enterRoom_withValidToken_receive200() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(1);

        MultiValueMap multiValueMap = new LinkedMultiValueMap();
        multiValueMap.set("roomId", roomInfo.getRoomId().toString());
        ResponseEntity<Object> response = enterRoom(multiValueMap, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void leaveRoom_withInvalidToken_receive401() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessageParams createMessage = new ChattingDto.SendMessageParams();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();
        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);
        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        clearInterceptors();

        authenticate("asda");

        ResponseEntity<Object> response = leaveRoom(createMessage.getRoomId(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void leaveRoom_whenBothUserInRoom_receive204() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessageParams createMessage = new ChattingDto.SendMessageParams();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();
        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);
        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        ResponseEntity<Object> response = leaveRoom(createMessage.getRoomId(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    private void connectStomp(String token) throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "bearer " + token);
        stompSession = stompClient.connect(new URI(String.format("http://localhost:%d/ws", port)), null, stompHeaders, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);
    }

    private void sendMessage(ChattingDto.SendMessageParams createMessage) {
        stompSession.send(API_V_1_CHATTINGS_SEND, createMessage);
    }

    private <T> ResponseEntity<T> enterRoom(MultiValueMap<String, String> params, Class<T> responseType) {
        String url = UriComponentsBuilder.fromPath(API_V_1_CHATTINGS + "/room/enter")
                .queryParams(params)
                .toUriString();
        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> leaveRoom (UUID roomId, Class<T> responseType) {
        return testRestTemplate.exchange(API_V_1_CHATTINGS + "/" + roomId, HttpMethod.DELETE, null, responseType);
    }

    private <T> ResponseEntity<T> getRooms (int page, int size, Class<T> responseType) {
        String url = API_V_1_CHATTINGS;
        url += "?page=" + page + "&size=" + size;

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    private <T> ResponseEntity<T> getRooms (int page, int size, ParameterizedTypeReference<T> responseType) {
        String url = API_V_1_CHATTINGS;
        url += "?page=" + page + "&size=" + size;

        return testRestTemplate.exchange(url, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> signUp(UserDto.Create dto, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_V_1_USERS, dto, responseType);
    }

    public <T> ResponseEntity<T> login(AuthDto.Login dto, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<AuthDto.Login> httpEntity = new HttpEntity<>(dto, headers);

        return testRestTemplate.postForEntity(API_1_0_AUTH_LOGIN, httpEntity, responseType);
    }

    private void authenticate(String token) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new RestTemplateInterceptor(token));
    }

    private void clearInterceptors() {
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    private RoomInfo sendToOnePersonHowManyMessagesAndAuthenticate(int howManyMessages) throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_SELLER_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_SELLER_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        UUID roomId = UUID.randomUUID();
        for (int i = 1; i <= howManyMessages; i++) {
            ChattingDto.SendMessageParams createMessage = new ChattingDto.SendMessageParams();
            createMessage.setMessage("HI" + i);
            createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            createMessage.setSenderId(sender.getId());
            createMessage.setRoomId(roomId);

            sendMessage(createMessage);

            blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        }

        authenticate(tokenResponse.getBody().getAccess_token());

        return RoomInfo.builder().roomId(roomId).build();
    }
}

@Getter
@Setter
@Builder
class RoomInfo {
    private UUID roomId;
}
