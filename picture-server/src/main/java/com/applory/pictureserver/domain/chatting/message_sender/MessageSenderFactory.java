package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingMessage;
import org.springframework.stereotype.Component;

// TODO - 객체를 찍어내는 곳은 아니라 팩토리라는 이름이 좀 애매하다. Map을 사용해서 서비스에서 처리하도록 바꾸는건 어떤가?
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
        || ChattingMessage.Type.DECLINE_MATCHING.equals(type)
        || ChattingMessage.Type.COMPLETE_MATCHING.equals(type)){
            return matchingMessageSender;
        }

        return textMessageSender;
    }
}
