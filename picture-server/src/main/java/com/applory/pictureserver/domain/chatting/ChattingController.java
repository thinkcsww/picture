package com.applory.pictureserver.domain.chatting;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
    public void sendMessage(@Valid ChattingDto.SendMessage createMessage) throws Exception {
        chattingService.send(createMessage);
    }

    @MessageMapping("/message-received")
    public void receivedMessage(@Valid ChattingDto.ReceiveMessage receiveMessage) {
        chattingService.receivedMessage(receiveMessage);
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
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoom enterRoom) {
        return chattingService.enterRoom(enterRoom);
    }


}
