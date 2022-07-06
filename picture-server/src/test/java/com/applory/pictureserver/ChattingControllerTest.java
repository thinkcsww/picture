package com.applory.pictureserver;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.OAuth2Token;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.*;

import static com.applory.pictureserver.TestConstants.*;
import static com.applory.pictureserver.TestConstants.TEST_USERNAME;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ChattingControllerTest {

    @LocalServerPort
    private Integer port;

    BlockingQueue<ChattingDto.Message> blockingQueue;
    WebSocketStompClient stompClient;
    StompSession stompSession;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private ChattingRoomMemberRepository chattingRoomMemberRepository;

    @Autowired
    private ChattingMessageRepository chattingMessageRepository;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        requestRepository.deleteAll();
        chattingMessageRepository.deleteAll();
        chattingRoomMemberRepository.deleteAll();
        chattingRoomRepository.deleteAll();
        userRepository.deleteAll();

    }

    @AfterEach
    public void cleanUp() {
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

        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        assertThat(stompSession).isNotNull();
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomIsCreated() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        assertThat(chattingRoomRepository.count()).isEqualTo(1);

    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomMemberIsCreated() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingRoomMemberRepository.count()).isEqualTo(2);
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomMemberIsConnectedWithRightChattingRoom() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingRoom chattingRoom = chattingRoomRepository.findAll().get(0);
        ChattingRoomMember chattingRoomMember = chattingRoomMemberRepository.findAll().get(0);
        assertThat(chattingRoomMember.getChattingRoom().getId()).isEqualTo(chattingRoom.getId());
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatMessageIsCreated() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatMessageIsConnectedWithRightChatRoom() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingRoom chattingRoom = chattingRoomRepository.findAll().get(0);
        ChattingMessage chattingMessage = chattingMessageRepository.findAll().get(0);
        assertThat(chattingMessage.getChattingRoom().getId()).isEqualTo(chattingRoom.getId());
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatMessageIsConnectedWithRightSenderAndReceiver() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setRoomId(UUID.randomUUID());
        message.setIsFirst(true);

        sendMessage(message);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingRoom chattingRoom = chattingRoomRepository.findAll().get(0);
        assertThat(chattingMessageRepository.findAll().get(0).getSender().getId()).isEqualTo(sender.getId());
        assertThat(chattingMessageRepository.findAll().get(0).getReceiver().getId()).isEqualTo(receiver.getId());
    }

    @Test
    public void sendMessage_withValidDto_sentMessageIsCorrect() throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {

        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.Message message = new ChattingDto.Message();
        message.setRoomId(UUID.randomUUID());
        message.setReceiverId(receiver.getId());
        message.setSenderId(sender.getId());
        message.setMessage("HI");
        message.setIsFirst(true);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        stompSession.subscribe("/room/" + message.getRoomId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChattingDto.Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((ChattingDto.Message) payload);
            }
        });


        stompSession.send("/api/v1/chat/send", message);


        assertThat(blockingQueue.poll(100, TimeUnit.MILLISECONDS).getMessage()).isEqualTo(message.getMessage());
    }


    @Test
    public void leaveRoom_whenBothUserInRoom_deleteRoomJoin() {

    }

    @Test
    public void leaveRoom_whenBothUserInRoom_updateMessageReadabilityToOnlyOpponentCanReadMessage() {

    }

    @Test
    public void leaveRoom_whenOnlyOneUserInRoom_deleteRoomJoin() {

    }

    @Test
    public void leaveRoom_whenOnlyOneUserInRoom_updateMessageReadabilityToNoOneCanRead() {

    }

    @Test
    public void startChatting_afterBothLeftRoom_useExistedRoomButCannotReadOldMessage() {

    }

    private void connectStomp(String token) throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "bearer " + token);
        stompSession = stompClient.connect(new URI(String.format("http://localhost:%d/ws", port)), null, stompHeaders, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);
    }

    private void sendMessage(ChattingDto.Message message) {
        stompSession.send("/api/v1/chat/send", message);
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
}
