package com.applory.pictureserver.domain.chatting;

import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chattings")
public class ChattingController {
    private final ChattingService chattingService;

    public ChattingController(ChattingService chattingService) {
        this.chattingService = chattingService;
    }

    @MessageMapping("/send")
    public void sendMessage(@Valid ChattingDto.SendMessageParams createMessage) throws Exception {
        chattingService.send(createMessage);
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveRoom(@PathVariable UUID roomId) {
        chattingService.leaveRoom(roomId);
    }

    @GetMapping("")
    public List<ChattingDto.ChattingRoomVM> getRooms(Pageable pageable) {
        return chattingService.getRooms(pageable);
    }

    @GetMapping("/{roomId}")
    public ChattingDto.ChattingRoomVM getRoom(@PathVariable UUID roomId) {
        return chattingService.getRoom(roomId);
    }

    @GetMapping("/room/enter")
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoomParams enterRoom) {
        return chattingService.enterRoom(enterRoom);
    }

    @GetMapping("/messages")
    public List<ChattingDto.MessageVM> getMessages(@RequestParam UUID roomId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS") LocalDateTime time) {
        return chattingService.getMessages(roomId, time);
    }


}
