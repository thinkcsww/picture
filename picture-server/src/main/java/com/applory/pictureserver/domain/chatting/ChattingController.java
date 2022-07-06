package com.applory.pictureserver.domain.chatting;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/send")
    public void sendMessage(@Valid ChattingDto.Message message) throws Exception {
        chattingService.send(message);
    }
}
