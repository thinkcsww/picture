package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.domain.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class ReviewDTO {
    @Getter
    @Setter
    public static class ReviewVM {
        private UUID id;

        private User seller;

        private User client;

        private String content;

        public ReviewVM(Review review) {
            this.id = review.getId();
            this.seller = review.getSeller();
            this.client = review.getClient();
            this.content = review.getContent();
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
