package com.applory.pictureserver.domain.favorite;

import lombok.Data;

import java.util.Objects;
import java.util.UUID;

public class FavoriteDTO {
    @Data
    public static class Toggle {
        private String userId;
        private String targetUserId;
    }

    @Data
    public static class VM {
        private String userId;
        private String nickname;
        private String fileName;
        private String id;

        public VM(Favorite favorite) {
            this.userId = favorite.getTargetUser().getId();
            this.nickname = favorite.getTargetUser().getNickname();
            this.id = favorite.getId();

            if (Objects.nonNull(favorite.getTargetUser().getFile())) {
                this.fileName = favorite.getTargetUser().getFile().getStoreFileName();
            }
        }
    }
}
