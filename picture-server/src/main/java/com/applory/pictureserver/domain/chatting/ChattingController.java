package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.shared.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chattings")
@RequiredArgsConstructor
public class ChattingController {
    private final ChattingService chattingService;

    @MessageMapping("/send")
    public void sendMessage(@Valid ChattingDto.SendMessageParams createMessage) {
        chattingService.send(createMessage);
    }

    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveRoom(@PathVariable UUID roomId) {
        chattingService.leaveRoom(roomId);
    }

    @GetMapping("")
    public Result<List<ChattingDto.ChattingRoomVM>> getRooms() {
        return Result.success(chattingService.getRooms());
    }

    @GetMapping("/room/enter")
    public ChattingDto.ChattingRoomVM enterRoom(ChattingDto.EnterRoomParams enterRoom) {
        return chattingService.enterRoom(enterRoom);
    }

    @GetMapping("/messages")
    public Page<ChattingDto.MessageVM> getMessages(@RequestParam UUID roomId, Pageable pageable) {
        return chattingService.getMessages(roomId, pageable);
    }

    @PostMapping("/{roomId}/photo")
    public Result<Object> sendPhoto(@Valid ChattingDto.SendMessageParams createMessage) {
        chattingService.send(createMessage);
        return Result.success();
    }
}
