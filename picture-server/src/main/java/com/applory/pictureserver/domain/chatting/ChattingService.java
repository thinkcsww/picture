package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.chatting.message_sender.MessageSender;
import com.applory.pictureserver.domain.chatting.message_sender.MessageSenderMapper;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingDto;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final MatchingRepository matchingRepository;

    private final UserRepository userRepository;

    private final MessageSenderMapper messageSenderMapper;

    public ChattingService(SimpMessagingTemplate simpMessagingTemplate, ChattingRoomRepository chattingRoomRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository, MatchingRepository matchingRepository, UserRepository userRepository, MessageSenderMapper messageSenderMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingRoomRepository = chattingRoomRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.matchingRepository = matchingRepository;
        this.userRepository = userRepository;
        this.messageSenderMapper = messageSenderMapper;
    }

    @Transactional
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoomParams enterRoom) {
        ChattingRoom chattingRoom = null;
        User opponent = null;
        Page<ChattingDto.MessageVM> messages = null;
        Optional<Matching> matchingInDB = Optional.empty();

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

            // 매칭 정보 조회
            matchingInDB = matchingRepository.findBySeller_IdAndClient_IdAndCompleteYN(chattingRoom.getSellerId(), chattingRoom.getClientId(), "N");

            // 입장 소켓 전송
            ChattingDto.StompMessageVM enterRoomMessageVM = ChattingDto.StompMessageVM.builder().messageType(ChattingMessage.Type.ENTER).senderId(currentUser.getId()).build();
            simpMessagingTemplate.convertAndSend("/room/" + chattingRoom.getId(), enterRoomMessageVM);
        } else {
            opponent = userRepository.findById(enterRoom.getTargetUserId())
                    .orElseThrow(() -> new IllegalStateException("User: " + enterRoom.getTargetUserId() + " not exist") );
        }

        return ChattingDto.ChattingRoomVM.builder()
                .id(chattingRoom == null ? UUID.randomUUID() : chattingRoom.getId())
                .opponent(new UserDto.VM(opponent))
                .messages(messages)
                .newRoom(chattingRoom == null)
                .matching(matchingInDB.map(MatchingDto.VM::new).orElse(null))
                .build();
    }

    @Transactional
    public void send(ChattingDto.SendMessageParams createMessage) {
        MessageSender messageSender = messageSenderMapper.find(createMessage.getMessageType());
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

    public List<ChattingDto.ChattingRoomVM> getRooms() {
        User user = userRepository.findByUsername(SecurityUtils.getPrincipal());

        // 2번 나눠서 쿼리 날려야하나?
        // room을 sellerId or clientId로 전체 조회
        // roomId list로 만들고
        List<UUID> roomIds = chattingRoomRepository.findAllByUser(user).stream()
                .map(ChattingRoom::getId)
                .collect(Collectors.toList());

        // in 절을 사용해서 fetch join
        return chattingRoomRepository.findAllByRoomIds(roomIds)
                .stream()
                .map(room -> {
                    ChattingMessage lastMessage = chattingMessageRepository.findTopByChattingRoomOrderByCreatedDtDesc(room);
                    int unreadCount = chattingMessageRepository.countUnreadMessageOfRoom(room.getId(), user.getId());

                    List<User> users = room.getChattingRoomMembers().stream()
                            .map(ChattingRoomMember::getUser)
                            .filter(u -> !u.getUsername().equals(SecurityUtils.getPrincipal()))
                            .collect(Collectors.toList());

                    User opponent = users.get(0);

                    return ChattingDto.ChattingRoomVM.builder()
                            .id(room.getId())
                            .lastMessage(new ChattingDto.MessageVM(lastMessage))
                            .lastMessageDt(lastMessage.getCreatedDt())
                            .unreadCount(unreadCount)
                            .opponent(new UserDto.VM(opponent))
                            .build();

                }).sorted(Comparator.comparing(ChattingDto.ChattingRoomVM::getLastMessageDt).reversed())
                .collect(Collectors.toList());
    }

    public Page<ChattingDto.MessageVM> getMessages(UUID roomId, Pageable pageable) {
        return chattingMessageRepository.findByChattingRoomIdOrderByCreatedDtDesc(roomId, pageable).map(ChattingDto.MessageVM::new);
    }
}
