package com.applory.pictureserver;

import com.applory.pictureserver.domain.chatting.ChattingDto;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.oauth.OAuth2Token;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
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
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException, TimeoutException {
        blockingQueue = new LinkedBlockingDeque<>();
        stompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    public void cleanUp() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
    }

    @Test
    public void startChatting_withInvalidToken_receiveUnauthorized() {
        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setRoomId("13");
        message.setSendTo("asd");

        try {
            connectStomp(null);
        } catch (Exception e) {
            assertThat(e.getMessage().contains("401")).isTrue();
        }

    }

    @Test
    public void startChatting_withValidToken_receiveOk() throws ExecutionException, InterruptedException, TimeoutException {

        signUp(TestUtil.createValidClientUser(TEST_USERNAME), Object.class);

        ResponseEntity<OAuth2Token> tokenResponse = login(TestUtil.createValidLoginDto(TEST_USERNAME), OAuth2Token.class);

        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("HI");
        message.setRoomId("13");
        message.setSendTo("asd");

        connectStomp(tokenResponse.getBody().getAccess_token());

        assertThat(stompSession).isNotNull();
    }

    @Test
    public void startChatting_whenRoomIsNotExistCreateRoom_receiveChatRoomVM() {

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

    @Test
    public void sendMessage_withValidDto_receiveOk() throws ExecutionException, InterruptedException, TimeoutException {


        ChattingDto.Message message = new ChattingDto.Message();
        message.setMessage("Test");
        message.setRoomId("1");

        stompSession.send("/api/v1/chat/send", message);

        stompSession.subscribe("/room/1", new StompFrameHandler() {
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
        assertThat(blockingQueue.poll(5, TimeUnit.SECONDS).getMessage()).isEqualTo(message.getMessage());
    }

    private void connectStomp(String token) throws ExecutionException, InterruptedException, TimeoutException {
        WebSocketHttpHeaders webSocketHttpHeaders = new WebSocketHttpHeaders();
        webSocketHttpHeaders.add("Authorization", "bearer " + token);
        stompSession = stompClient.connect(String.format("http://localhost:%d/ws", port), webSocketHttpHeaders, new StompSessionHandlerAdapter() {}).get(1, TimeUnit.SECONDS);
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
