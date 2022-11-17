package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingMessage;
import com.applory.pictureserver.domain.chatting.ChattingMessageRepository;
import com.applory.pictureserver.domain.chatting.ChattingRoomMemberRepository;
import com.applory.pictureserver.domain.chatting.ChattingRoomRepository;
import com.applory.pictureserver.domain.matching.MatchingRepository;
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

    private final MatchingRepository matchingRepository;

    public MessageSenderFactory(ChattingRoomRepository chattingRoomRepository, SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository, ChattingRoomMemberRepository chattingRoomMemberRepository, ChattingMessageRepository chattingMessageRepository, MatchingRepository matchingRepository) {
        this.chattingRoomRepository = chattingRoomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.chattingRoomMemberRepository = chattingRoomMemberRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.matchingRepository = matchingRepository;
    }

    public MessageSender build(ChattingMessage.Type type) {

        MessageSender messageSender = new TextMessageSender(chattingRoomRepository, simpMessagingTemplate, userRepository, chattingRoomMemberRepository, chattingMessageRepository);

        if (ChattingMessage.Type.ENTER.equals(type)) {
            messageSender = new EnterMessageSender(simpMessagingTemplate);
        } else if (ChattingMessage.Type.RECEIVE.equals(type)) {
            messageSender = new ReceiveMessageSender(simpMessagingTemplate, chattingMessageRepository);
        } else if (ChattingMessage.Type.REQUEST_MATCHING.equals(type)
        || ChattingMessage.Type.ACCEPT_MATCHING.equals(type)
        || ChattingMessage.Type.DECLINE_MATCHING.equals(type)){
            messageSender = new MatchingMessageSender(simpMessagingTemplate, chattingRoomRepository, chattingMessageRepository, userRepository, matchingRepository);
        }

        return messageSender;
    }
}
