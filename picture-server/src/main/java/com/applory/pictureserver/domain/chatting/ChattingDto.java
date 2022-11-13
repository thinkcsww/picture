package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ChattingDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SendMessage {
        @NotNull
        private UUID roomId;

        @NotNull
        private UUID senderId;

        private List<UUID> userIdList;

        private UUID sellerId;

        private UUID clientId;

        private String message;

        private ChattingRoom.Type roomType;

        private ChattingMessage.Type messageType;

        private UUID id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ReceiveMessage {
        @NotNull
        private UUID roomId;

        @NotNull
        private UUID senderId;

        @NotNull
        private UUID messageId;

        @NotNull
        private ChattingMessage.Type messageType;
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
    public static class EnterRoomMessageVM {
        private ChattingMessage.Type messageType;
        private UUID senderId;

        public EnterRoomMessageVM(ChattingMessage.Type messageType, UUID senderId) {
            this.messageType = messageType;
            this.senderId = senderId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MessageVM {
        private UUID senderId;
        private String message;
        private String createdDt;
        private String readBy;
        private ChattingMessage.Type type;

        public MessageVM(ChattingMessage message) {
            this.senderId = message.getSender().getId();
            this.message = message.getMessage();
            this.createdDt = message.getCreatedDt().toString();
            this.readBy = message.getReadBy();
            this.type = message.getType();
        }
    }

    @Getter
    @Setter
    @Builder
    public static class ChattingRoomVM {
        private UUID id;

        private User opponent;

        private LocalDateTime lastMessageDt;

        private String lastMessage;

        private Integer unreadCount;

        private List<MessageVM> messages;

        private boolean newRoom;
    }
}
