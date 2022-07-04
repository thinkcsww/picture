package com.applory.pictureserver.domain.chatting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChattingDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message {
        private String roomId;



        private String sendTo;

        private String message;

    }
}
