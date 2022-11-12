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

        @NotNull
        private UUID senderId;

        private List<UUID> userIdList;

        private UUID sellerId;

        private UUID clientId;

        private String message;

        private ChattingRoom.Type roomType;
    }

    @Getter
    @Setter
    public static class EnterRoom {
        private UUID roomId;

        private UUID targetUserId;

        private UUID sellerId;

        private UUID clientId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessageVM {
        private UUID senderId;
        private String message;
        private String createdDt;

        public MessageVM(ChattingMessage message) {
            this.senderId = message.getSender().getId();
            this.message = message.getMessage();
            this.createdDt = message.getCreatedDt().toString();
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

        private List<MessageVM> messages;

        private boolean newRoom;
    }
}
