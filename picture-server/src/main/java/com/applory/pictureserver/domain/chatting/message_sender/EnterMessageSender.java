package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class EnterMessageSender implements MessageSender {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public EnterMessageSender(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                .messageType(sendMessageParams.getMessageType())
                .senderId(sendMessageParams.getSenderId())
                .build();

        simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
    }
}
