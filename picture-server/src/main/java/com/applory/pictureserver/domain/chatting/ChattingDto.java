package com.applory.pictureserver.domain.chatting;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class ChattingDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Message {
        @NotNull
        private UUID roomId;

        @NotNull
        private UUID receiverId;

        @NotNull
        private UUID senderId;

        private String message;

        private Boolean isFirst;

    }

    @Getter
    @Setter
    public static class ChattingRoomVM {
        private UUID id;

        private String opponentNickname;

        private LocalDateTime lastMessageDt;

        private String lastMessage;

        private Integer unreadCount;

    }
}
