package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.shared.Constant;
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
    public static class SendMessageParams {
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

        private UUID messageId;

        private Constant.Specialty specialty;

        private Integer price;

        private LocalDateTime dueDate;

    }

    @Getter
    @Setter
    public static class EnterRoomParams {
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
        private String readBy;
        private ChattingMessage.Type messageType;

        public MessageVM(ChattingMessage message) {
            this.senderId = message.getSender().getId();
            this.message = message.getMessage();
            this.createdDt = message.getCreatedDt().toString();
            this.readBy = message.getReadBy();
            this.messageType = message.getMessageType();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class StompMessageVM {
        @NotNull
        private UUID roomId;

        @NotNull
        private UUID senderId;

        private UUID sellerId;

        private UUID clientId;

        private String message;

        private ChattingRoom.Type roomType;

        private ChattingMessage.Type messageType;

        private UUID id;


        @Builder
        public StompMessageVM(UUID roomId, UUID senderId, UUID sellerId, UUID clientId, String message, ChattingRoom.Type roomType, ChattingMessage.Type messageType, UUID id) {
            this.roomId = roomId;
            this.senderId = senderId;
            this.sellerId = sellerId;
            this.clientId = clientId;
            this.message = message;
            this.roomType = roomType;
            this.messageType = messageType;
            this.id = id;
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

        private Page<MessageVM> messages;

        private boolean newRoom;
    }
}
