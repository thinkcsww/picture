package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingDto;
import com.applory.pictureserver.domain.chatting.ChattingMessage;
import com.applory.pictureserver.domain.chatting.ChattingMessageRepository;
import com.applory.pictureserver.exception.NotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ReceiveMessageSender implements MessageSender{

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingMessageRepository chattingMessageRepository;

    public ReceiveMessageSender(SimpMessagingTemplate simpMessagingTemplate, ChattingMessageRepository chattingMessageRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chattingMessageRepository = chattingMessageRepository;
    }

    @Override
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingMessage chattingMessage = chattingMessageRepository.findById(sendMessageParams.getMessageId()).orElseThrow(() -> new NotFoundException("Message does not exist: " + sendMessageParams.getMessageId()));
        chattingMessage.setReadBy(sendMessageParams.getSenderId().toString());

        ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                .messageType(ChattingMessage.Type.RECEIVE)
                .build();

        simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
    }
}
