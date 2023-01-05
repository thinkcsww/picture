package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.file.FileService;
import com.applory.pictureserver.shared.Result;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;
import sun.misc.IOUtils;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/chattings")
public class ChattingController {
    private final ChattingService chattingService;

    private final FileService fileService;

    public ChattingController(ChattingService chattingService, FileService fileService) {
        this.chattingService = chattingService;
        this.fileService = fileService;
    }

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
    public List<ChattingDto.ChattingRoomVM> getRooms() {
        return chattingService.getRooms();
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

    @ResponseBody
    @GetMapping("/images/{filename}")
    public byte[] downloadImage(@PathVariable String filename) throws IOException {
//        return new UrlResource("file:" + fileService.getFullPath(filename));

        InputStream in = getClass()
                .getClassLoader()
                .getResourceAsStream("f65c4613-e8f7-4e46-b836-3482b9729b5f.jpg");
        return IOUtils.readAllBytes(in);
    }


}
