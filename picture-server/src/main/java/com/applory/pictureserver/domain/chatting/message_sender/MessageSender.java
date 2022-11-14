package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingDto;

public interface MessageSender {
    void sendMessage(ChattingDto.SendMessageParams sendMessageParams);
}
