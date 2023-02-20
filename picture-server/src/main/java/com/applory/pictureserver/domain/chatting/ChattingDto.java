package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.matching.MatchingDto;
import com.applory.pictureserver.domain.user.UserDto;
import com.applory.pictureserver.shared.Constant;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

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
        private String roomId;

        @NotNull
        private UUID senderId;

        private List<UUID> userIdList;

        private UUID sellerId;

        private UUID clientId;

        private String message;

        private ChattingRoom.Type roomType;

        private ChattingMessage.Type messageType;

        private String messageId;

        private Constant.Specialty specialty;

        private Integer price;

        private LocalDateTime dueDate;

        private String requestComment;

        private MultipartFile attachFile;

        private String fileName;

        private String requestId;

    }

    @Getter
    @Setter
    public static class EnterRoomParams {
        private String roomId;

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
        private String fileName;

        public MessageVM(ChattingMessage message) {
            this.senderId = message.getSender().getId();
            this.message = message.getMessage();
            this.createdDt = message.getCreatedDt().toString();
            this.readBy = message.getReadBy();
            this.messageType = message.getMessageType();
            if (message.getFile() != null) {
                this.fileName = message.getFile().getStoreFileName();
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class StompMessageVM {
        @NotNull
        private String roomId;

        @NotNull
        private UUID senderId;

        private UUID sellerId;

        private UUID clientId;

        private String message;

        private ChattingRoom.Type roomType;

        private ChattingMessage.Type messageType;

        private String id;

        private String fileName;

        @Builder
        public StompMessageVM(String roomId, UUID senderId, UUID sellerId, UUID clientId, String message, ChattingRoom.Type roomType, ChattingMessage.Type messageType, String id, String fileName) {
            this.roomId = roomId;
            this.senderId = senderId;
            this.sellerId = sellerId;
            this.clientId = clientId;
            this.message = message;
            this.roomType = roomType;
            this.messageType = messageType;
            this.id = id;
            this.fileName = fileName;
        }
    }

    @Getter
    @Setter
    @Builder
    public static class ChattingRoomVM {
        private String id;

        private UserDto.VM opponent;

        private LocalDateTime lastMessageDt;

        private ChattingDto.MessageVM lastMessage;

        private Integer unreadCount;

        private Page<MessageVM> messages;

        private boolean newRoom;

        private MatchingDto.VM matching;
    }
}
