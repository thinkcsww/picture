package com.applory.pictureserver.domain.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class RequestDto {

    @Getter
    @Setter
    public static class Create {
        @NotNull
        private Request.RequestType requestType;

        @NotNull
        private String title;

        @NotNull
        private Integer desiredPrice;

        @NotNull
        private LocalDateTime dueDate;

        @NotNull
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VM {
        private UUID id;
        private UUID userId;
        private String userNickname;
        private Request.RequestType requestType;
        private String title;
        private Integer desiredPrice;
        private LocalDateTime dueDate;
        private String description;
        private Integer readCount;

        public VM(Request request) {
            this.id = request.getId();
            this.userId = request.getUser().getId();
            this.userNickname = request.getUser().getNickname();
            this.requestType = request.getRequestType();
            this.title = request.getTitle();
            this.desiredPrice = request.getDesiredPrice();
            this.dueDate = request.getDueDate();
            this.description = request.getDescription();
            this.readCount = request.getReadCount();
        }
    }

    @Getter
    @Setter
    public static class Search {
        private Request.RequestType requestType;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime fromForDueDt;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime toForDueDt;

    }


}
