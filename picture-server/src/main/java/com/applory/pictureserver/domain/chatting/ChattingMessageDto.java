package com.applory.pictureserver.domain.chatting;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class ChattingMessageDto {

    @Getter
    @Setter
    public static class Search {
        private String roomId;
        private UUID userId;
        private String readBy;
    }
}
