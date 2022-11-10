package com.applory.pictureserver.domain.chatting;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChattingDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateMessage {
        @NotNull
        private UUID roomId;

        private List<UUID> userIdList;

        @NotNull
        private UUID senderId;

        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessageVM {
        private UUID senderId;
        private String message;

        public MessageVM(ChattingMessage chattingMessage) {
            this.senderId = chattingMessage.getSender().getId();
            this.message = chattingMessage.getMessage();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class ChattingRoomVM {
        private UUID id;

        private String opponentNickname;

        private LocalDateTime lastMessageDt;

        private String lastMessage;

        private Integer unreadCount;

        private Page<MessageVM> messages;

        private boolean isNew;


    }
}
