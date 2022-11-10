package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.exception.NotFoundException;
import com.applory.pictureserver.domain.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    @Transactional
    public void send(ChattingDto.CreateMessage createMessage) {

        ChattingRoom targetChattingRoom = null;

        // 개인톡방 생성시

        // 이미 방이 있는 경우
        if (createMessage.getUserIdList() == null) {
            targetChattingRoom = chattingRoomRepository.getById(createMessage.getRoomId());
        } else {
            // 재사용 가능한 방이 있는지 flag
            boolean recyclable = false;

            User user = userRepository.getById(createMessage.getSenderId());
            List<ChattingRoom> chattingRoomsInDB = chattingRoomRepository.findAllByChattingRoomMembers_UserAndChattingRoomMembers_UseYN(user, "N");

            for (ChattingRoom chattingRoom : chattingRoomsInDB) {
                String idConcat = chattingRoom.getChattingRoomMembers().stream().map(chattingRoomMember -> chattingRoomMember.getUser().getId().toString()).reduce("", String::concat);

                if (idConcat.contains(createMessage.getUserIdList().get(0).toString()) && idConcat.contains(createMessage.getUserIdList().get(1).toString())) {
                    recyclable = true;
                    targetChattingRoom = chattingRoom;
                    break;
                }
            }

            // 재사용 가능한 방이 없으면 생성
            if (!recyclable) {
                targetChattingRoom = saveNewRoom(createMessage);

                targetChattingRoom.setChattingRoomMembers(saveNewRoomMember(createMessage, targetChattingRoom));
            }
        }

        for (ChattingRoomMember chattingRoomMember : targetChattingRoom.getChattingRoomMembers()) {
            if (chattingRoomMember.getUseYN().equals("N")) {
                chattingRoomMember.setUseYN("Y");
            }
        }

        saveMessage(createMessage, targetChattingRoom);

        simpMessagingTemplate.convertAndSend("/room/" + createMessage.getRoomId(), createMessage);
    }

    @Transactional
    ChattingRoom saveNewRoom(ChattingDto.CreateMessage createMessage) {
        ChattingRoom chattingRoom;
        chattingRoom = new ChattingRoom();
        chattingRoom.setId(createMessage.getRoomId());

        return chattingRoomRepository.save(chattingRoom);
    }

    @Transactional
    List<ChattingRoomMember> saveNewRoomMember(ChattingDto.CreateMessage createMessage, ChattingRoom chattingRoomInDB) {
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
    void saveMessage(ChattingDto.CreateMessage createMessage, ChattingRoom chattingRoomInDB) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage(createMessage.getMessage());
        chattingMessage.setSender(userRepository.findById(createMessage.getSenderId()).get());
        chattingMessage.setVisibleTo("ALL");
        chattingMessageRepository.save(chattingMessage);
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

                    String opponentNickname = "";

                    for (ChattingRoomMember member : room.getChattingRoomMembers()) {
                        if (!member.getUser().getId().equals(user.getId())) {
                            opponentNickname = member.getUser().getNickname();
                        }
                    }

                    return ChattingDto.ChattingRoomVM.builder()
                            .id(room.getId())
                            .lastMessage(lastMessage.getMessage())
                            .lastMessageDt(lastMessage.getCreatedDt())
                            .unreadCount(unreadCount)
                            .opponentNickname(opponentNickname)
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

    public ChattingDto.ChattingRoomVM getRoomByTargetUser(UUID userId) {
        User targetUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User " + userId + "not exist"));
        Optional<ChattingRoom> chattingRoomOptional = chattingRoomRepository.findByChattingRoomMembers_User(targetUser);

        Page<ChattingDto.MessageVM> messages = null;
        if (chattingRoomOptional.isPresent()) {
            messages = chattingMessageRepository.findByChattingRoomId(chattingRoomOptional.get().getId(), SecurityUtils.getPrincipal(), PageRequest.of(0, 20, Sort.Direction.DESC, "createdDt"))
                    .map(ChattingDto.MessageVM::new);
        }

        return ChattingDto.ChattingRoomVM.builder()
                .id(chattingRoomOptional.isPresent() ? chattingRoomOptional.get().getId() : UUID.randomUUID())
                .opponentNickname(targetUser.getNickname())
                .messages(messages)
                .isNew(!chattingRoomOptional.isPresent())
                .build();
    }
}
