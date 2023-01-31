package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.TestConstants;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
    private ChattingRoomMemberRepository chattingRoomMemberRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private ChattingMessageRepository chattingMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @DisplayName("채팅시작")
    @Nested
    class CreateChattingRoom {

        @DisplayName("채팅시작 - 방이 없을 경우 새로 생성")
        @Test
        void startChatting_whenRoomIsNotExist_createRoom() {
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();
            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            assertThat(chattingRoomRepository.findAll()).isNotEmpty();
        }

        @DisplayName("채팅 시작 - 채팅시작시 채팅방 멤버 생성")
        @Test
        void startChatting_createChattingRoomMember() {
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();
            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            ChattingRoom chattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).get();
            List<ChattingRoomMember> senderRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, sender);
            List<ChattingRoomMember> clientRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, receiver);

            assertThat(senderRoomMember).isNotEmpty();
            assertThat(clientRoomMember).isNotEmpty();
            assertThat(senderRoomMember.get(0).getUseYN()).isEqualTo("Y");
            assertThat(clientRoomMember.get(0).getUseYN()).isEqualTo("Y");
        }

        @DisplayName("채팅 시작 - 메세지가 알맞게 전송됨")
        @Test
        void startChatting_messageIsSent() {
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();
            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            List<ChattingMessage> message = chattingMessageRepository.findByChattingRoom_Id(sendMessageParams.getRoomId());
            assertThat(message.get(0).getMessage()).isEqualTo("HI");
            assertThat(message.get(0).getSender().getId()).isEqualTo(sender.getId());
            assertThat(message.get(0).getVisibleTo()).isEqualTo(ChattingMessage.VisibleToType.ALL.toString());
        }

        @WithMockClientLogin
        @DisplayName("채팅 시작 - 두명 모두 퇴장한 상태에서 다시 채팅 시작시 방을 재활용")
        @Test
        void startChatting_whenBothUserLeaveRoom_reUseRoom() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 1명 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            // 2명 퇴장
            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_SELLER_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            // 다시 채팅 시작
            chattingService.send(sendMessageParams);


            assertThat(chattingRoomRepository.findAll().size()).isEqualTo(1);
        }

        @WithMockClientLogin
        @DisplayName("채팅 시작 - 나간 사람 모두 useYN을 Y로 수정")
        @Test
        void startChatting_whenBothUserLeaveRoom_updateBothRoomMembersUseYN() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 1명 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            // 2명 퇴장
            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_SELLER_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            // 다시 채팅 시작
            chattingService.send(sendMessageParams);

            ChattingRoom chattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).get();
            List<ChattingRoomMember> senderRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, sender);
            List<ChattingRoomMember> clientRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, receiver);

            assertThat(senderRoomMember.get(0).getUseYN()).isEqualTo("Y");
            assertThat(clientRoomMember.get(0).getUseYN()).isEqualTo("Y");
        }


        @DisplayName("채팅 시작 - Request에서 채팅시작시 Request의 ChatCount++")
        @Test
        void startChatting_whenRequestIdIsSent_createRoomAndAddRequestsChatCount() {
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

    @DisplayName("채팅방 퇴장")
    @Nested
    class LeaveRoom {

        @WithMockClientLogin
        @DisplayName("채팅방 퇴장 - 둘 다 방에 있을 경우 나가는 ChattingRoomMember의 useYN을 N으로")
        @Test
        void leaveRoom_whenBothUserInRoom_setLeavingUsersUseYnToN() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            ChattingRoom chattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).get();
            List<ChattingRoomMember> senderRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, sender);
            List<ChattingRoomMember> clientRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, receiver);

            assertThat(senderRoomMember.get(0).getUseYN()).isEqualTo("Y");
            assertThat(clientRoomMember.get(0).getUseYN()).isEqualTo("N");
        }

        @WithMockClientLogin
        @DisplayName("채팅방 퇴장 - 나간 사람은 메세지를 더 이상 못보도록 visibleTo 업데이트")
        @Test
        void leaveRoom_whenBothUserInRoom_updateMessageVisible() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            ChattingMessage chattingMessage = chattingMessageRepository.findAll().get(0);
            assertThat(chattingMessage.getVisibleTo()).isEqualTo(sender.getId().toString());
        }

        @WithMockClientLogin
        @DisplayName("채팅방 퇴장 - 두명 모두 퇴장시 ChattingRoomMember 모두 useYN을 N으로 수정")
        @Test
        void leaveRoom_whenBothUserLeaveRoom_updateBothRoomMembersUseYN() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 1명 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            // 2명 퇴장
            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_SELLER_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            ChattingRoom chattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).get();
            List<ChattingRoomMember> senderRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, sender);
            List<ChattingRoomMember> clientRoomMember = chattingRoomMemberRepository.findByChattingRoomAndUser(chattingRoom, receiver);

            assertThat(senderRoomMember.get(0).getUseYN()).isEqualTo("N");
            assertThat(clientRoomMember.get(0).getUseYN()).isEqualTo("N");
        }

        @WithMockClientLogin
        @DisplayName("채팅방 퇴장 - 두명 모두 퇴장시 메세지의 visibleTo는 None으로")
        @Test
        void leaveRoom_whenBothUserLeaveRoom_updateMessageVisibleToNone() {
            // 입장
            User sender = TestUtil.createSeller(TEST_SELLER_USERNAME, Constant.Specialty.BACKGROUND);
            User receiver = TestUtil.createClient();

            userRepository.save(sender);
            userRepository.save(receiver);

            ChattingDto.SendMessageParams sendMessageParams = new ChattingDto.SendMessageParams();
            sendMessageParams.setMessage("HI");
            sendMessageParams.setUserIdList(Arrays.asList(sender.getId(), receiver.getId()));
            sendMessageParams.setSenderId(sender.getId());
            sendMessageParams.setRoomId(UUID.randomUUID());

            chattingService.send(sendMessageParams);

            // 퇴장
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            Authentication auth = new UsernamePasswordAuthenticationToken(TestConstants.TEST_SELLER_USERNAME, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
            SecurityContextHolder.getContext().setAuthentication(auth);
            chattingService.leaveRoom(sendMessageParams.getRoomId());

            ChattingMessage chattingMessage = chattingMessageRepository.findAll().get(0);

            assertThat(chattingMessage.getVisibleTo()).isEqualTo(ChattingMessage.VisibleToType.NONE.toString());
        }
    }

    @DisplayName("채팅방 입장")
    @Nested
    class EnterRoom {
    }


}
