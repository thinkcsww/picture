package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingMessage;
import com.applory.pictureserver.domain.chatting.ChattingMessageRepository;
import com.applory.pictureserver.domain.chatting.ChattingRoomMemberRepository;
import com.applory.pictureserver.domain.chatting.ChattingRoomRepository;
import com.applory.pictureserver.domain.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageSenderFactory {

    private final ChattingRoomRepository chattingRoomRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    public MessageSenderFactory(ChattingRoomRepository chattingRoomRepository, SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository) {
        this.chattingRoomRepository = chattingRoomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
    }


    public MessageSender build(ChattingMessage.Type type) {

        MessageSender messageSender = new TextMessageSender(chattingRoomRepository, simpMessagingTemplate, userRepository, chattingRoomMemberRepository, chattingMessageRepository);

        if (ChattingMessage.Type.ENTER.equals(type)) {
            messageSender = new EnterMessageSender(simpMessagingTemplate);
        } else if (ChattingMessage.Type.RECEIVE.equals(type)) {
            messageSender = new ReceiveMessageSender(simpMessagingTemplate, chattingMessageRepository);
        }

        return messageSender;
    }
}
