package com.applory.pictureserver.domain.chatting;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chattings")
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/send")
    public void sendMessage(@Valid ChattingDto.Message message) throws Exception {
        chattingService.send(message);
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveRoom(@PathVariable UUID roomId) {
        chattingService.leaveRoom(roomId);
    }

    @GetMapping("/{roomId}")
    public void getRoom(@PathVariable UUID roomId) {

    }
}
