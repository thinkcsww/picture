package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    public ChattingService(SimpMessagingTemplate simpMessagingTemplate, ChattingRoomRepository chattingRoomRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository, UserRepository userRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingRoomRepository = chattingRoomRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoom enterRoom) {
        ChattingRoom chattingRoom = null;
        User opponent = null;
        List<ChattingDto.MessageVM> messages = null;

        if (enterRoom.getRoomId() != null) {
            chattingRoom = chattingRoomRepository.findById(enterRoom.getRoomId()).orElseThrow(() -> new NotFoundException("Room does not exist: " + enterRoom.getRoomId()));
        } else {
            chattingRoom = chattingRoomRepository.findBySellerIdAndClientId(enterRoom.getSellerId(), enterRoom.getClientId()).orElse(null);
        }

        if (chattingRoom != null) {
            // 가장 최근 20개의 메세지만 조회
            messages = chattingMessageRepository.findTop20ByChattingRoomAndCreatedDtBeforeOrderByCreatedDtDesc(chattingRoom, LocalDateTime.now())
                    .stream().sorted(Comparator.comparing(ChattingMessage::getCreatedDt))
                    .map(ChattingDto.MessageVM::new)
                    .collect(Collectors.toList());

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
    public void receivedMessage(ChattingDto.ReceiveMessage receiveMessage) {
        ChattingMessage chattingMessage = chattingMessageRepository.findById(receiveMessage.getMessageId()).orElseThrow(() -> new NotFoundException("Message does not exist: " + receiveMessage.getMessageId()));
        chattingMessage.setReadBy(receiveMessage.getSenderId().toString());

        simpMessagingTemplate.convertAndSend("/room/" + receiveMessage.getRoomId(), receiveMessage);
    }

    @Transactional
    public void send(ChattingDto.SendMessage createMessage) {
        ChattingRoom targetChattingRoom = chattingRoomRepository.findById(createMessage.getRoomId())
                .orElseGet(() -> {

                    // 개인 채팅일 경우 재사용 가능한 방이 있는지 확인
                    if (ChattingRoom.Type.PRIVATE.equals(createMessage.getRoomType())) {
                        Optional<ChattingRoom> optionalChattingRoom = chattingRoomRepository.findBySellerIdAndClientId(createMessage.getSellerId(), createMessage.getClientId());
                        if (optionalChattingRoom.isPresent()) {
                            return optionalChattingRoom.get();
                        }
                    }

                    return saveNewRoom(createMessage);
                });


        targetChattingRoom.getChattingRoomMembers()
                .forEach(chattingRoomMember -> chattingRoomMember.setUseYN("Y"));

        ChattingMessage chattingMessage = saveMessage(createMessage, targetChattingRoom);
        createMessage.setId(chattingMessage.getId());

        simpMessagingTemplate.convertAndSend("/room/" + createMessage.getRoomId(), createMessage);
    }

    @Transactional
    ChattingRoom saveNewRoom(ChattingDto.SendMessage createMessage) {
        ChattingRoom chattingRoom;
        chattingRoom = new ChattingRoom();
        chattingRoom.setId(createMessage.getRoomId());
        chattingRoom.setType(createMessage.getRoomType());

        if (ChattingRoom.Type.PRIVATE.equals(createMessage.getRoomType())) {
            chattingRoom.setClientId(createMessage.getClientId());
            chattingRoom.setSellerId(createMessage.getSellerId());
        }

        ChattingRoom newChattingRoom = chattingRoomRepository.save(chattingRoom);
        newChattingRoom.setChattingRoomMembers(saveNewRoomMember(createMessage, newChattingRoom));
        return newChattingRoom;
    }

    @Transactional
    List<ChattingRoomMember> saveNewRoomMember(ChattingDto.SendMessage createMessage, ChattingRoom chattingRoomInDB) {
        List<ChattingRoomMember> chattingRoomMembers = new ArrayList<>();
        for (UUID userId : createMessage.getUserIdList()) {
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                throw new NotFoundException("Send Message: " + userId + " is not exist");
            }

            ChattingRoomMember chattingRoomMember = new ChattingRoomMember();
            chattingRoomMember.setChattingRoom(chattingRoomInDB);
            chattingRoomMember.setUser(userOptional.get());
            chattingRoomMember.setUseYN("Y");
            chattingRoomMembers.add(chattingRoomMember);
        }
        return chattingRoomMemberRepository.saveAll(chattingRoomMembers);
    }

    @Transactional
    ChattingMessage saveMessage(ChattingDto.SendMessage sendMessage, ChattingRoom chattingRoomInDB) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage(sendMessage.getMessage());
        chattingMessage.setType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.findById(sendMessage.getSenderId()).get());
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
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

    public ChattingDto.ChattingRoomVM getRoom(UUID roomId) {
        ChattingRoom chattingRoom = chattingRoomRepository.findById(roomId).orElseThrow(() -> new NotFoundException("Room " + roomId + " does not exist"));

//        List<ChattingDto.MessageVM> messages = chattingMessageRepository.findByChattingRoom_Id(roomId).stream().map(ChattingDto.MessageVM::new).collect(Collectors.toList());

        return ChattingDto.ChattingRoomVM.builder()
                .id(roomId)
//                .messages(messages)
                .build();
    }
}
