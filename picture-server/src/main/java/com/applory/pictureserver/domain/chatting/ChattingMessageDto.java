package com.applory.pictureserver.domain.chatting;

import lombok.Getter;
import lombok.Setter;


public class ChattingMessageDto {

    @Getter
    @Setter
    public static class Search {
        private String roomId;
        private String userId;
        private String readBy;
    }
}
