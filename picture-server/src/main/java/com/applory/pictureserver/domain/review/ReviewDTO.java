package com.applory.pictureserver.domain.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReviewDTO {
    @Getter
    @Setter
    public static class ReviewVM {
        private UUID id;

        private String writerNickname;

        private String content;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdDt;

        public ReviewVM(Review review) {
            this.id = review.getId();
            this.writerNickname = review.getClient().getNickname();
            this.content = review.getContent();
            this.createdDt = review.getCreatedDt();
        }
    }

    @Data
    public static class Create {
        @NotEmpty
        private UUID sellerId;

        @NotEmpty
        private String content;

        @NotNull
        private Integer rate;
    }
}
