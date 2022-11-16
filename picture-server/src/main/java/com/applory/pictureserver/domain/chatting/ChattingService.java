package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.chatting.message_sender.MessageSender;
import com.applory.pictureserver.domain.chatting.message_sender.MessageSenderFactory;
import com.applory.pictureserver.domain.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    private final MessageSenderFactory messageSenderFactory;

    public ChattingService(SimpMessagingTemplate simpMessagingTemplate, ChattingRoomRepository chattingRoomRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository, UserRepository userRepository, MessageSenderFactory messageSenderFactory) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingRoomRepository = chattingRoomRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.userRepository = userRepository;
        this.messageSenderFactory = messageSenderFactory;
    }

    @Transactional
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoomParams enterRoom) {
        ChattingRoom chattingRoom = null;
        User opponent = null;
        Page<ChattingDto.MessageVM> messages = null;

        if (enterRoom.getRoomId() != null) {
            chattingRoom = chattingRoomRepository.findById(enterRoom.getRoomId()).orElseThrow(() -> new NotFoundException("Room does not exist: " + enterRoom.getRoomId()));
        } else {
            chattingRoom = chattingRoomRepository.findBySellerIdAndClientId(enterRoom.getSellerId(), enterRoom.getClientId()).orElse(null);
        }

        if (chattingRoom != null) {
            // 가장 최근 20개의 메세지만 조회
            messages = chattingMessageRepository.findByChattingRoomIdOrderByCreatedDtDesc(chattingRoom.getId(), PageRequest.of(0, 20, Sort.Direction.DESC, "createdDt"))
                    .map(ChattingDto.MessageVM::new);

            // 방 입장시 상대방 메세지 읽음 처리
            User currentUser = userRepository.findByUsername(SecurityUtils.getPrincipal());
            chattingMessageRepository.findAllByChattingRoomAndReadByIsNullAndSenderNot(chattingRoom, currentUser)
                    .forEach(m -> m.setReadBy(currentUser.getId().toString()));


            // 상대방 정보 조회
            opponent = chattingRoom.getChattingRoomMembers().stream()
                    .map(ChattingRoomMember::getUser)
                    .filter(user -> !user.getUsername().equals(SecurityUtils.getPrincipal()))
                    .collect(Collectors.toList()).get(0);

            // 입장 소켓 전송
            ChattingDto.EnterRoomMessageVM enterRoomMessageVM = new ChattingDto.EnterRoomMessageVM(ChattingMessage.Type.ENTER, currentUser.getId());
            simpMessagingTemplate.convertAndSend("/room/" + chattingRoom.getId(), enterRoomMessageVM);
        }

        return ChattingDto.ChattingRoomVM.builder()
                .id(chattingRoom == null ? UUID.randomUUID() : chattingRoom.getId())
                .opponent(opponent)
                .messages(messages)
                .newRoom(chattingRoom == null)
                .build();
    }

    @Transactional
    public void send(ChattingDto.SendMessageParams createMessage) {
        MessageSender messageSender = messageSenderFactory.build(createMessage.getMessageType());
        messageSender.sendMessage(createMessage);

    }

    @Transactional
    public void leaveRoom(UUID roomId) {
        String username = SecurityUtils.getPrincipal();

        User user = userRepository.findByUsername(username);

        List<ChattingRoomMember> roomMembers = chattingRoomMemberRepository.findByChattingRoom_IdAndUseYN(roomId, "Y");

        UUID leftUserId = null;

        for (ChattingRoomMember roomMember : roomMembers) {
            if (roomMember.getUser().getId().equals(user.getId())) {
                roomMember.setUseYN("N");
            } else {
                leftUserId = roomMember.getUser().getId();
            }
        }

        List<ChattingMessage> messages = chattingMessageRepository.findByChattingRoom_Id(roomId);

        for (ChattingMessage message : messages) {
            if (leftUserId != null) {
                message.setVisibleTo(leftUserId.toString());
            } else {
                message.setVisibleTo(ChattingMessage.VisibleToType.NONE.toString());
            }
        }

    }

    public List<ChattingDto.ChattingRoomVM> getRooms(Pageable pageable) {
        User user = userRepository.findByUsername(SecurityUtils.getPrincipal());

        return chattingRoomRepository.findAllByChattingRoomMembers_User(user, pageable)
                .stream()
                .map(room -> {
                    ChattingMessage lastMessage = chattingMessageRepository.findTopByChattingRoomOrderByCreatedDtDesc(room);
                    int unreadCount = chattingMessageRepository.countUnreadMessageOfRoom(room.getId(), user.getId());

                    User opponent = room.getChattingRoomMembers().stream()
                            .map(ChattingRoomMember::getUser)
                            .filter(u -> !u.getUsername().equals(SecurityUtils.getPrincipal()))
                            .collect(Collectors.toList()).get(0);

                    return ChattingDto.ChattingRoomVM.builder()
                            .id(room.getId())
                            .lastMessage(lastMessage.getMessage())
                            .lastMessageDt(lastMessage.getCreatedDt())
                            .unreadCount(unreadCount)
                            .opponent(opponent)
                            .build();

                }).sorted(Comparator.comparing(ChattingDto.ChattingRoomVM::getLastMessageDt).reversed())
                .collect(Collectors.toList());
    }

    public Page<ChattingDto.MessageVM> getMessages(UUID roomId, Pageable pageable) {
        return chattingMessageRepository.findByChattingRoomIdOrderByCreatedDtDesc(roomId, pageable).map(ChattingDto.MessageVM::new);
    }
}
