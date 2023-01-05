package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.*;
import com.applory.pictureserver.domain.file.FileService;
import com.applory.pictureserver.domain.file.File;
import com.applory.pictureserver.domain.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ImageMessageSender implements MessageSender {

    private final ChattingRoomRepository chattingRoomRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final UserRepository userRepository;

    private final ChattingMessageRepository chattingMessageRepository;

    private final FileService fileService;

    public ImageMessageSender(ChattingRoomRepository chattingRoomRepository, SimpMessagingTemplate simpMessagingTemplate, UserRepository userRepository, ChattingMessageRepository chattingMessageRepository, FileService fileStore) {
        this.chattingRoomRepository = chattingRoomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.userRepository = userRepository;
        this.chattingMessageRepository = chattingMessageRepository;
        this.fileService = fileStore;
    }

    @Override
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        ChattingRoom targetChattingRoom = chattingRoomRepository.findById(sendMessageParams.getRoomId()).orElseThrow(() -> new IllegalStateException("Room: " + sendMessageParams.getRoomId() + " not exist"));
        targetChattingRoom.getChattingRoomMembers()
                .forEach(chattingRoomMember -> chattingRoomMember.setUseYN("Y"));

        try {
            File fileInDB = fileService.storeFile(sendMessageParams.getAttachFile());
            ChattingMessage chattingMessage = saveMessage(sendMessageParams, targetChattingRoom, fileInDB);
            sendMessageParams.setMessageId(chattingMessage.getId());

            ChattingDto.StompMessageVM stompMessageVM = ChattingDto.StompMessageVM.builder()
                    .senderId(sendMessageParams.getSenderId())
                    .roomType(ChattingRoom.Type.PRIVATE)
                    .messageType(chattingMessage.getMessageType())
                    .message(chattingMessage.getMessage())
                    .id(chattingMessage.getId())
                    .filePath(fileService.getFullPath(fileInDB.getStoreFileName()))
                    .build();

            simpMessagingTemplate.convertAndSend("/room/" + sendMessageParams.getRoomId(), stompMessageVM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    ChattingMessage saveMessage(ChattingDto.SendMessageParams sendMessage, ChattingRoom chattingRoomInDB, File file) {
        ChattingMessage chattingMessage = new ChattingMessage();
        chattingMessage.setChattingRoom(chattingRoomInDB);
        chattingMessage.setMessage("");
        chattingMessage.setMessageType(ChattingMessage.Type.IMAGE);
        chattingMessage.setSender(userRepository.getById(sendMessage.getSenderId()));
        chattingMessage.setVisibleTo("ALL");
        chattingMessage.setFile(file);
        return chattingMessageRepository.save(chattingMessage);
    }
}
