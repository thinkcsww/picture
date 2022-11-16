package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.Matching;
import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.transaction.Transactional;

public class MatchingMessageSender implements MessageSender {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final UserRepository userRepository;

    public MatchingMessageSender(SimpMessagingTemplate simpMessagingTemplate, ChattingRoomRepository chattingRoomRepository, ChattingMessageRepository chattingMessageRepository, UserRepository userRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingRoomRepository = chattingRoomRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingRoom targetChattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).orElseThrow(() -> new NotFoundException("Rood does not exist: " + sendMessageParams.getRoomId()));

        if (sendMessageParams.getMessageType().equals(ChattingMessage.Type.REQUEST_MATCHING)) {
            Matching matching = Matching.builder()
                    .client(userRepository.getById(sendMessageParams.getClientId()))
                    .seller(userRepository.getById(sendMessageParams.getSellerId()))
                    .completeYN("M")
                    .dueDate(sendMessageParams.getDueDate())
                    .price(sendMessageParams.getPrice())
                    .specialty(sendMessageParams.getSpecialty())
                    .build();


        }
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
    ChattingMessage saveMessage(ChattingDto.SendMessageParams sendMessage, ChattingRoom chattingRoom) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoom);
        chattingMessage.setMessage(sendMessage.getMessage());
        chattingMessage.setType(sendMessage.getMessageType());
        chattingMessage.setSender(userRepository.findById(sendMessage.getSenderId()).get());
        chattingMessage.setVisibleTo("ALL");
        return chattingMessageRepository.save(chattingMessage);
    }
}
