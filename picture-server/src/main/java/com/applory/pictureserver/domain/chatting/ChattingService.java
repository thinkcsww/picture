package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.exception.NotFoundException;
import com.applory.pictureserver.domain.shared.SecurityUtils;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    @Transactional
    public void send(ChattingDto.Message message) {

        ChattingRoom targetChattingRoom = null;

        // 개인톡방 생성시
        if (message.getUserIdList() == null) {
            targetChattingRoom = chattingRoomRepository.getById(message.getRoomId());
        } else {
            // 재사용 가능한 방이 있는지 flag
            boolean recyclable = false;

            User user = userRepository.getById(message.getSenderId());
            List<ChattingRoom> chattingRoomsInDB = chattingRoomRepository.findAllByChattingRoomMembers_UserAndChattingRoomMembers_UseYN(user, "N");

            for (ChattingRoom chattingRoom : chattingRoomsInDB) {
                String idConcat = chattingRoom.getChattingRoomMembers().stream().map(chattingRoomMember -> chattingRoomMember.getUser().getId().toString()).reduce("", String::concat);

                if (idConcat.contains(message.getUserIdList().get(0).toString()) && idConcat.contains(message.getUserIdList().get(1).toString())) {
                    recyclable = true;
                    targetChattingRoom = chattingRoom;
                    break;
                }
            }

            // 재사용 가능한 방이 없으면 생성
            if (!recyclable) {
                targetChattingRoom = saveNewRoom(message);

                targetChattingRoom.setChattingRoomMembers(saveNewRoomMember(message, targetChattingRoom));
            }
        }

        for (ChattingRoomMember chattingRoomMember : targetChattingRoom.getChattingRoomMembers()) {
            if (chattingRoomMember.getUseYN().equals("N")) {
                chattingRoomMember.setUseYN("Y");
            }
        }

        saveMessage(message, targetChattingRoom);

        simpMessagingTemplate.convertAndSend("/room/" + message.getRoomId(), message);
    }

    @Transactional
    ChattingRoom saveNewRoom(ChattingDto.Message message) {
        ChattingRoom chattingRoom;
        chattingRoom = new ChattingRoom();
        chattingRoom.setId(message.getRoomId());

        return chattingRoomRepository.save(chattingRoom);
    }

    @Transactional
    List<ChattingRoomMember> saveNewRoomMember(ChattingDto.Message message, ChattingRoom chattingRoomInDB) {
        List<ChattingRoomMember> chattingRoomMembers = new ArrayList<>();
        for (UUID userId : message.getUserIdList()) {
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
    void saveMessage(ChattingDto.Message message, ChattingRoom chattingRoomInDB) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage(message.getMessage());
        chattingMessage.setSender(userRepository.findById(message.getSenderId()).get());
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

    public Page<ChattingDto.ChattingRoomVM> getRooms(Pageable pageable) {
        String username = SecurityUtils.getPrincipal();
        User user = userRepository.findByUsername(username);

        Page<ChattingRoom> chattingRoomsInDB = chattingRoomRepository.findAllByChattingRoomMembers_User(user, pageable);


        return chattingRoomsInDB.map(room -> {
            ChattingDto.ChattingRoomVM chattingRoomVM = new ChattingDto.ChattingRoomVM();

            ChattingMessage lastMessage = chattingMessageRepository.findTopByChattingRoom(room);

            int unreadCount = chattingMessageRepository.countUnreadMessageOfRoom(room.getId(), user.getId());

            String opponentNickname = "";

            for (ChattingRoomMember member : room.getChattingRoomMembers()) {
                if (!member.getUser().getId().equals(user.getId())) {
                    opponentNickname = member.getUser().getNickname();
                }
            }


            chattingRoomVM.setId(room.getId());
            chattingRoomVM.setLastMessage(lastMessage.getMessage());
            chattingRoomVM.setLastMessageDt(lastMessage.getCreatedDt());
            chattingRoomVM.setUnreadCount(unreadCount);
            chattingRoomVM.setOpponentNickname(opponentNickname);

            return chattingRoomVM;
        });
    }
}
