package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.request.Request;
import com.applory.pictureserver.domain.request.RequestRepository;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.*;


@RequiredArgsConstructor
@Component
public class TextMessageSender implements MessageSender {

    private final ChattingRoomRepository chattingRoomRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final RequestRepository requestRepository;

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
                .roomId(sendMessageParams.getRoomId())
                .senderId(sendMessageParams.getSenderId())
                .roomType(sendMessageParams.getRoomType())
                .messageType(chattingMessage.getMessageType())
                .message(chattingMessage.getMessage())
                .id(chattingMessage.getId())
                .build();


        simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
        String targetUserId = targetChattingRoom.getClientId().equals(sendMessageParams.getSenderId()) ? targetChattingRoom.getSellerId().toString() : targetChattingRoom.getClientId().toString();
        simpMessagingTemplate.convertAndSend("/chat-list/" + targetUserId, stompMessageVM);
    }

    private ChattingRoom saveNewRoom(ChattingDto.SendMessageParams createMessage) {
        ChattingRoom chattingRoom;
        chattingRoom = new ChattingRoom();
        chattingRoom.setId(createMessage.getRoomId());
        chattingRoom.setType(createMessage.getRoomType());
        chattingRoom.setClientId(createMessage.getClientId());
        chattingRoom.setSellerId(createMessage.getSellerId());

        // Request에서 채팅 생성시 Request의 채팅카운트 ++
        if (Objects.nonNull(createMessage.getRequestId())) {
            Request request = requestRepository.findById(createMessage.getRequestId()).orElseThrow(() -> new NoSuchElementException("Request id: " + createMessage.getRequestId() + " not exist"));
            request.setChatCount(request.getChatCount() + 1);
        }

        ChattingRoom newChattingRoom = chattingRoomRepository.save(chattingRoom);
        newChattingRoom.setChattingRoomMembers(saveNewRoomMember(createMessage, newChattingRoom));
        return newChattingRoom;
    }

    private List<ChattingRoomMember> saveNewRoomMember(ChattingDto.SendMessageParams createMessage, ChattingRoom chattingRoomInDB) {
        List<ChattingRoomMember> chattingRoomMembers = new ArrayList<>();
        for (String userId : createMessage.getUserIdList()) {
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

    private ChattingMessage saveMessage(ChattingDto.SendMessageParams sendMessage, ChattingRoom chattingRoomInDB) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage(sendMessage.getMessage());
        chattingMessage.setMessageType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.getById(sendMessage.getSenderId()));
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
    }
}
