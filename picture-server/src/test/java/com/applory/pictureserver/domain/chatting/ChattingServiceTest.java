package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.domain.request.Request;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.shared.Constant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static com.applory.pictureserver.TestConstants.TEST_SELLER_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChattingServiceTest {

    @Autowired
    private ChattingService chattingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @DisplayName("채팅방 생성")
    @Nested
    class CreateChattingRoom {
        @WithMockClientLogin
        @DisplayName("채팅방 생성 - Request에서 채팅방 생성시 Request의 ChatCount++")
        @Test
        void createRoom_whenRequestIdIsSent_createRoomAndAddRequestsChatCount() {
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            Request request = TestUtil.createRequest(receiver, LocalDateTime.now().plusHours(10), "title", "desc", Constant.Specialty.BACKGROUND, 1000);
            requestRepository.save(request);


            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());
            sendMessageParams.setRequestId(request.getId());

            chattingService.send(sendMessageParams);

            assertThat(requestRepository.findById(request.getId()).get().getChatCount()).isEqualTo(1);
        }
    }

//    @DisplayName("채팅방 입장")
//    @Nested
//    class EnterRoom {
//        @WithMockClientLogin
//        @DisplayName("채팅방 입장 - Request에서 채팅방 생성시 Request의 ChatCount++")
//        @Test
//        void createRoom_whenRequestIdIsSent_createRoomAndAddRequestsChatCount() {
//            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
//            User receiver = TestUtil.createClient();
//
//            userRepository.save(sender);
//            userRepository.save(receiver);
//
//            Request request = TestUtil.createRequest(receiver, LocalDateTime.now().plusHours(10), "title", "desc", Constant.Specialty.BACKGROUND, 1000);
//            requestRepository.save(request);
//
//
//            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
//            sendMessageParams.setMessage("HI");
//            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
//            sendMessageParams.setSenderId(sender.getId());
//            sendMessageParams.setRoomId(UUID.randomUUID());
//            sendMessageParams.setRequestId(request.getId());
//
//            chattingService.send(sendMessageParams);
//
//            assertThat(requestRepository.findById(request.getId()).get().getChatCount()).isEqualTo(1);
//        }
//    }


}
