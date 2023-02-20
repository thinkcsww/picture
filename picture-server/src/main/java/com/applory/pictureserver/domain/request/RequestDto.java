package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.shared.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RequestDto {

    @Getter
    @Setter
    public static class Create {
        @NotNull
        private Constant.Specialty specialty;

        @NotNull
        private String title;

        @NotNull
        private Integer desiredPrice;

        @NotNull
        private LocalDateTime dueDate;

        @NotNull
        private String description;

        private String matchYn;

        private String completeYn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VM {
        private String id;
        private String userId;
        private String userProfileFileName;
        private String userNickname;
        private Double userAcceptRate;
        private Constant.Specialty specialty;
        private String title;
        private Integer desiredPrice;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime dueDate;
        private String description;
        private String matchYn;
        private Integer readCount;
        private Integer chatCount;
        private List<RequestDto.VM> anotherRequests;

        public VM(Request request) {
            this.id = request.getId();
            this.userId = request.getUser().getId();
            this.userNickname = request.getUser().getNickname();
            this.specialty = request.getSpecialty();
            this.title = request.getTitle();
            this.desiredPrice = request.getDesiredPrice();
            this.dueDate = request.getDueDate();
            this.description = request.getDescription();
            this.readCount = request.getReadCount();
            this.matchYn = request.getMatchYN();
            this.chatCount = request.getChatCount();

            if (Objects.nonNull(request.getUser().getFile())) {
                this.userProfileFileName = request.getUser().getFile().getStoreFileName();
            }
        }
    }

    @Getter
    @Setter
    public static class Search {
        private Constant.Specialty specialty;


        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime fromForDueDt;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime toForDueDt;

        private String userId;

        private String exceptThisId;

    }


}
