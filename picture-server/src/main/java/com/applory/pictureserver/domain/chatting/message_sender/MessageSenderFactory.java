package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingMessage;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderFactory {

    private final TextMessageSender textMessageSender;

    private final EnterMessageSender enterMessageSender;

    private final ReceiveMessageSender receiveMessageSender;

    private final MatchingMessageSender matchingMessageSender;

    public MessageSenderFactory(TextMessageSender textMessageSender, EnterMessageSender enterMessageSender, ReceiveMessageSender receiveMessageSender, MatchingMessageSender matchingMessageSender) {
        this.textMessageSender = textMessageSender;
        this.enterMessageSender = enterMessageSender;
        this.receiveMessageSender = receiveMessageSender;
        this.matchingMessageSender = matchingMessageSender;
    }

    public MessageSender build(ChattingMessage.Type type) {
        if (ChattingMessage.Type.ENTER.equals(type)) {
            return enterMessageSender;
        } else if (ChattingMessage.Type.RECEIVE.equals(type)) {
            return receiveMessageSender;
        } else if (ChattingMessage.Type.REQUEST_MATCHING.equals(type)
        || ChattingMessage.Type.ACCEPT_MATCHING.equals(type)
        || ChattingMessage.Type.DECLINE_MATCHING.equals(type)){
            return matchingMessageSender;
        }

        return textMessageSender;
    }
}
