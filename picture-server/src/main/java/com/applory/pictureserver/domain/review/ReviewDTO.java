package com.applory.pictureserver.domain.review;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class ReviewDTO {
    @Getter
    @Setter
    public static class ReviewVM {
        private String id;

        private String writerNickname;

        private String content;

        private int rate;

        private String writerProfileImageFileName;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createdDt;

        public ReviewVM(Review review) {
            this.id = review.getId();
            this.writerNickname = review.getClient().getNickname();
            this.content = review.getContent();
            this.createdDt = review.getCreatedDt();
            this.rate = review.getRate();

            if (Objects.nonNull(review.getClient().getFile())) {
                this.writerProfileImageFileName = review.getClient().getFile().getStoreFileName();
            }
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

    @Data
    public static class Search {
        @NotEmpty
        private UUID sellerId;
    }
}
