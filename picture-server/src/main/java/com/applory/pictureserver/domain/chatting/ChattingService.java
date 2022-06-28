package com.applory.pictureserver.domain.chatting;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ChattingService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final ChattingRoomRepository chattingRoomRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    public void send(ChattingDto.Message message) {
        if (!StringUtils.hasLength(message.getRoomId())) {
            // room 생성

            // roomMember 생성

            // message 생성
        }
        simpMessagingTemplate.convertAndSend("/room/" + message.getRoomId(), message);
    }
}
