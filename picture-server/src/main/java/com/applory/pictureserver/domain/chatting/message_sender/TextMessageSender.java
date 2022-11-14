package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TextMessageSender implements MessageSender {

    private final ChattingRoomRepository chattingRoomRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    public TextMessageSender(ChattingRoomRepository chattingRoomRepository, SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository) {
        this.chattingRoomRepository = chattingRoomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
    }

    @Override
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingRoom targetChattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId())
                .orElseGet(() -> {

                    // 개인 채팅일 경우 재사용 가능한 방이 있는지 확인
                    if (ChattingRoom.Type.PRIVATE.equals(sendMessageParams.getRoomType())) {
                        Optional<ChattingRoom> optionalChattingRoom = chattingRoomRepository.findBySellerIdAndClientId(sendMessageParams.getSellerId(), sendMessageParams.getClientId());
                        if (optionalChattingRoom.isPresent()) {
                            return optionalChattingRoom.get();
                        }
                    }

                    return saveNewRoom(sendMessageParams);
                });


        targetChattingRoom.getChattingRoomMembers()
                .forEach(chattingRoomMember -> chattingRoomMember.setUseYN("Y"));

        ChattingMessage chattingMessage = saveMessage(sendMessageParams, targetChattingRoom);
        sendMessageParams.setMessageId(chattingMessage.getId());

        ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                .senderId(sendMessageParams.getSenderId())
                .roomType(sendMessageParams.getRoomType())
                .messageType(chattingMessage.getType())
                .message(chattingMessage.getMessage())
                .id(chattingMessage.getId())
                .build();


        simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
    }

    @Transactional
    ChattingRoom saveNewRoom(ChattingDto.SendMessageParams createMessage) {
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
    List<ChattingRoomMember> saveNewRoomMember(ChattingDto.SendMessageParams createMessage, ChattingRoom chattingRoomInDB) {
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
    ChattingMessage saveMessage(ChattingDto.SendMessageParams sendMessage, ChattingRoom chattingRoomInDB) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage(sendMessage.getMessage());
        chattingMessage.setType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.findById(sendMessage.getSenderId()).get());
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
    }
}
