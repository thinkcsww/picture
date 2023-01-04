package com.applory.pictureserver.domain.chatting.message_sender;

import com.applory.pictureserver.domain.chatting.ChattingDto;
import com.applory.pictureserver.domain.file.FileStore;
import com.applory.pictureserver.domain.file.UploadFile;

import java.io.IOException;

public class ImageMessageSender implements MessageSender {

    private final FileStore fileStore;

    public ImageMessageSender(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    @Override
    public void sendMessage(ChattingDto.SendMessageParams sendMessageParams) {
        try {
            UploadFile attachFile = fileStore.storeFile(sendMessageParams.getAttachFile());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
