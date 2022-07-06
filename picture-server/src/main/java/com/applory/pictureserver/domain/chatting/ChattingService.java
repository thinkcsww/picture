package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.exception.NotFoundException;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingRoomMemberRepository chattingRoomMemberRepository;

    private final ChattingMessageRepository chattingMessageRepository;


    private final UserRepository userRepository;

    @Transactional
    public void send(ChattingDto.Message message) {
        if (message.getIsFirst() != null && message.getIsFirst()) {
            // room 생성
            ChattingRoom chattingRoom = new ChattingRoom();
            chattingRoom.setId(message.getRoomId());
            ChattingRoom chattingRoomInDB = chattingRoomRepository.save(chattingRoom);

            // roomMember 생성
            Optional<User> sender = userRepository.findById(message.getSenderId());
            Optional<User> receiver = userRepository.findById(message.getReceiverId());

            if (sender.isPresent() && receiver.isPresent()) {
                ChattingRoomMember chattingRoomMemberSender = new ChattingRoomMember();
                chattingRoomMemberSender.setChattingRoom(chattingRoomInDB);
                chattingRoomMemberSender.setUser(sender.get());
                chattingRoomMemberRepository.save(chattingRoomMemberSender);

                ChattingRoomMember chattingRoomMemberReceiver = new ChattingRoomMember();
                chattingRoomMemberReceiver.setChattingRoom(chattingRoomInDB);
                chattingRoomMemberReceiver.setUser(receiver.get());
                chattingRoomMemberRepository.save(chattingRoomMemberReceiver);

                ChattingMessage chattingMessage = new ChattingMessage();
                chattingMessage.setChattingRoom(chattingRoomInDB);
                chattingMessage.setMessage(message.getMessage());
                chattingMessage.setReceiver(receiver.get());
                chattingMessage.setSender(sender.get());
                chattingMessageRepository.save(chattingMessage);

            } else {
                throw new NotFoundException("sender or receiver is not exist");
            }


            // message 생성
        }
        simpMessagingTemplate.convertAndSend("/room/" + message.getRoomId(), message);
    }
}
