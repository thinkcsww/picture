package com.applory.pictureserver;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.MyOAuth2Token;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.domain.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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

    BlockingQueue<ChattingDto.SendMessage> blockingQueue;
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
        clearInterceptors();
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

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        assertThat(stompSession).isNotNull();
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomIsCreated() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        assertThat(chattingRoomRepository.count()).isEqualTo(1);

    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomMemberIsCreated() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingRoomMemberRepository.count()).isEqualTo(2);
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomMemberIsCreatedAndUseYnIsSetToY() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingRoomMemberRepository.findAll().get(0).getUseYN()).isEqualTo("Y");
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatroomMemberIsConnectedWithRightChattingRoom() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

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

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingMessageRepository.count()).isEqualTo(1);
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatMessageIsConnectedWithRightChatRoom() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingRoom chattingRoom = chattingRoomRepository.findAll().get(0);
        ChattingMessage chattingMessage = chattingMessageRepository.findAll().get(0);
        assertThat(chattingMessage.getChattingRoom().getId()).isEqualTo(chattingRoom.getId());
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_chatMessageIsConnectedWithRightSender() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        assertThat(chattingMessageRepository.findAll().get(0).getSender().getId()).isEqualTo(sender.getId());
    }

    @Test
    public void startChatting_whenILeftThisRoomBefore_recycleTheRoomAlreadyExist() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ResponseEntity<MyOAuth2Token> tokenResponse2 = login(TestUtil.createValidLoginDto(user2.getUsername()), MyOAuth2Token.class);
        String token2 = tokenResponse2.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        leaveRoom(createMessage.getRoomId(), Object.class);

        ChattingDto.SendMessage createMessage2 = new ChattingDto.SendMessage();
        createMessage2.setRoomId(UUID.randomUUID());
        createMessage2.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage2.setSenderId(sender.getId());
        createMessage2.setMessage("HI");

        sendMessage(createMessage2);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        Optional<ChattingRoom> chattingRoomOptional = chattingRoomRepository.findById(createMessage.getRoomId());
        assertThat(chattingRoomOptional.isPresent()).isTrue();
    }

    @Test
    public void startChatting_whenILeftThisRoomBefore_everyRoomMemberUseYnIsY() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        leaveRoom(createMessage.getRoomId(), Object.class);

        ChattingDto.SendMessage createMessage2 = new ChattingDto.SendMessage();
        createMessage2.setRoomId(UUID.randomUUID());
        createMessage2.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage2.setSenderId(sender.getId());
        createMessage2.setMessage("HI");

        sendMessage(createMessage2);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        List<ChattingRoomMember> all = chattingRoomMemberRepository.findByChattingRoom_IdAndUseYN(createMessage.getRoomId(), "Y");

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void sendMessage_whenOpponentLeftRoom_everyRoomMemberUseYnIsY() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ResponseEntity<MyOAuth2Token> tokenResponse2 = login(TestUtil.createValidLoginDto(user2.getUsername()), MyOAuth2Token.class);
        String token2 = tokenResponse2.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        clearInterceptors();

        // 2번쨰 leaveRoom 시작
        authenticate(token2);
        leaveRoom(createMessage.getRoomId(), Object.class);

        // 나로 다시 로그인
        clearInterceptors();
        authenticate(token);

        ChattingDto.SendMessage createMessage2 = new ChattingDto.SendMessage();
        createMessage2.setRoomId(createMessage.getRoomId());
        createMessage2.setSenderId(sender.getId());
        createMessage2.setMessage("HI");

        sendMessage(createMessage2);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        List<ChattingRoomMember> all = chattingRoomMemberRepository.findByChattingRoom_IdAndUseYN(createMessage.getRoomId(), "Y");

        assertThat(all.size()).isEqualTo(2);
    }

    @Test
    public void sendMessage_withValidDto_sentMessageIsCorrect() throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {

        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        stompSession.subscribe("/room/" + createMessage.getRoomId(), new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChattingDto.SendMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((ChattingDto.SendMessage) payload);
            }
        });


        sendMessage(createMessage);


        assertThat(blockingQueue.poll(100, TimeUnit.MILLISECONDS).getMessage()).isEqualTo(createMessage.getMessage());
    }

    @Test
    public void getRooms_withInvalidToken_receive401() {
        authenticate("asda");
        ResponseEntity<Object> response = getRooms(0, 5, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getRooms_withValidToken_receive200() {
        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<Object> response = getRooms(0, 5, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void getRooms_withValidToken_receivePagedRoomVmList() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});

        assertThat(roomsResponse.getBody().get(0).getId()).isEqualTo(roomInfo.getRoomId());
    }

    @Test
    public void getRooms_withValidToken_receiveRoomsOrderByLatestTime() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.Create user3 = TestUtil.createValidClientUser(TEST_USERNAME + "3");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();
        UserDto.VM receiver2 = signUp(user3, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setMessage("HI");
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setRoomId(UUID.randomUUID());

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        ChattingDto.SendMessage createMessage2 = new ChattingDto.SendMessage();
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

    @Test
    public void getRooms_withValidToken_receiveRoomWithLastMessageAndSentTime() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});

        assertThat(roomsResponse.getBody().get(0).getLastMessage()).isEqualTo("HI2");
    }

    @Test
    public void getRooms_withValidToken_receiveRoomWithUnreadCountWhenIamSender() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getUnreadCount()).isEqualTo(0);
    }

    @Test
    public void getRooms_withValidToken_receiveRoomWithUnreadCountWhenIamReceiver() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME + "2"), MyOAuth2Token.class);
        clearInterceptors();
        authenticate(tokenResponse.getBody().getAccess_token());

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getUnreadCount()).isEqualTo(2);
    }

    @Test
    public void getRooms_withValidToken_receiveRoomWithOpponentNickname() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        sendToOnePersonHowManyMessagesAndAuthenticate(2);

        ResponseEntity<List<ChattingDto.ChattingRoomVM>> roomsResponse = getRooms(0, 10, new ParameterizedTypeReference<List<ChattingDto.ChattingRoomVM>>() {});
        assertThat(roomsResponse.getBody().get(0).getOpponent()).isNotNull();
    }

    @Test
    public void enterRoom_withInvalidToken_receive401() {
        authenticate("asda");

        ResponseEntity<Object> response = enterRoom(UUID.randomUUID(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void enterRoom_withValidToken_receive200() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(1);

        ResponseEntity<Object> response = enterRoom(roomInfo.getRoomId(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void enterRoom_withValidToken_receiveRoomVM() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(1);

        ResponseEntity<ChattingDto.ChattingRoomVM> response = enterRoom(roomInfo.getRoomId(), ChattingDto.ChattingRoomVM.class);

        assertThat(response.getBody().getId()).isEqualTo(roomInfo.getRoomId());
    }

    @Test
    public void enterRoom_withValidToken_receiveRoomVMWithMessages() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {

        RoomInfo roomInfo = sendToOnePersonHowManyMessagesAndAuthenticate(1);

        ResponseEntity<ChattingDto.ChattingRoomVM> response = enterRoom(roomInfo.getRoomId(), ChattingDto.ChattingRoomVM.class);

        assertThat(response.getBody().getMessages().size()).isEqualTo(1);

    }


    @Test
    public void leaveRoom_withInvalidToken_receive401() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
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
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();
        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);
        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        ResponseEntity<Object> response = leaveRoom(createMessage.getRoomId(), Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    public void leaveRoom_whenBothUserInRoom_setRoomMemberUseYnToN() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();
        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);
        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        leaveRoom(createMessage.getRoomId(), Object.class);

        List<ChattingRoomMember> roomMembers = chattingRoomMemberRepository.findByChattingRoom_IdAndUseYN(createMessage.getRoomId(), "Y");

        assertThat(roomMembers.size()).isEqualTo(1);

    }

    @Test
    public void leaveRoom_whenBothUserInRoom_updateMessageVisibleToRoomMemberWhoLeft() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();
        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);
        blockingQueue.poll(100, TimeUnit.MILLISECONDS);
        leaveRoom(createMessage.getRoomId(), Object.class);

        boolean flag = true;
        List<ChattingMessage> messages = chattingMessageRepository.findByChattingRoom_Id(createMessage.getRoomId());

        for (ChattingMessage m : messages) {
            if (!m.getVisibleTo().equals(receiver.getId().toString())) {
                flag = false;
            }
        }

        assertThat(flag).isTrue();
    }

    @Test
    public void leaveRoom_whenOnlyOneUserIsInRoom_everyRoomMemberUseYnIsN() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ResponseEntity<MyOAuth2Token> tokenResponse2 = login(TestUtil.createValidLoginDto(user2.getUsername()), MyOAuth2Token.class);
        String token2 = tokenResponse2.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        leaveRoom(createMessage.getRoomId(), Object.class);

        clearInterceptors();

        // 2번쨰 leaveRoom 시작
        authenticate(token2);
        leaveRoom(createMessage.getRoomId(), Object.class);

        List<ChattingRoomMember> roomMembers = chattingRoomMemberRepository.findByChattingRoom_IdAndUseYN(createMessage.getRoomId(), "N");

        assertThat(roomMembers.size()).isEqualTo(2);
    }

    @Test
    public void leaveRoom_whenOnlyOneUserInRoom_updateMessageReadabilityToNoOneCanRead() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ResponseEntity<MyOAuth2Token> tokenResponse2 = login(TestUtil.createValidLoginDto(user2.getUsername()), MyOAuth2Token.class);
        String token2 = tokenResponse2.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        leaveRoom(createMessage.getRoomId(), Object.class);

        clearInterceptors();

        // 2번쨰 leaveRoom 시작
        authenticate(token2);
        leaveRoom(createMessage.getRoomId(), Object.class);

        List<ChattingMessage> messages = chattingMessageRepository.findByChattingRoom_Id(createMessage.getRoomId());

        assertThat(messages.get(0).getVisibleTo()).isEqualTo(ChattingMessage.VisibleToType.NONE.toString());
    }

    @Test
    public void startChatting_afterBothLeftRoom_useExistedRoomButCannotReadOldMessage() throws URISyntaxException, ExecutionException, InterruptedException, TimeoutException {
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");

        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(user1.getUsername()), MyOAuth2Token.class);
        String token = tokenResponse.getBody().getAccess_token();

        ResponseEntity<MyOAuth2Token> tokenResponse2 = login(TestUtil.createValidLoginDto(user2.getUsername()), MyOAuth2Token.class);
        String token2 = tokenResponse2.getBody().getAccess_token();

        ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
        createMessage.setRoomId(UUID.randomUUID());
        createMessage.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
        createMessage.setSenderId(sender.getId());
        createMessage.setMessage("HI");

        authenticate(token);
        connectStomp(token);

        sendMessage(createMessage);

        blockingQueue.poll(100, TimeUnit.MILLISECONDS);

        leaveRoom(createMessage.getRoomId(), Object.class);

        clearInterceptors();

        // 2번쨰 leaveRoom 시작
        authenticate(token2);
        leaveRoom(createMessage.getRoomId(), Object.class);

        sendMessage(createMessage);


    }

    private void connectStomp(String token) throws ExecutionException, InterruptedException, TimeoutException, URISyntaxException {
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "bearer " + token);
        stompSession = stompClient.connect(new URI(String.format("http://localhost:%d/ws", port)), null, stompHeaders, new StompSessionHandlerAdapter() {}).get(5, TimeUnit.SECONDS);
    }

    private void sendMessage(ChattingDto.SendMessage createMessage) {
        stompSession.send(API_V_1_CHATTINGS_SEND, createMessage);
    }

    private <T> ResponseEntity<T> enterRoom (UUID roomId, Class<T> responseType) {
        return testRestTemplate.exchange(API_V_1_CHATTINGS + "/" + roomId, HttpMethod.GET, null, responseType);
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
        UserDto.Create user1 = TestUtil.createValidClientUser(TEST_USERNAME);
        UserDto.Create user2 = TestUtil.createValidClientUser(TEST_USERNAME + "2");
        UserDto.VM sender = signUp(user1, UserDto.VM.class).getBody();
        UserDto.VM receiver = signUp(user2, UserDto.VM.class).getBody();

        ResponseEntity<MyOAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), MyOAuth2Token.class);

        connectStomp(tokenResponse.getBody().getAccess_token());

        UUID roomId = UUID.randomUUID();
        for (int i = 1; i <= howManyMessages; i++) {
            ChattingDto.SendMessage createMessage = new ChattingDto.SendMessage();
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
