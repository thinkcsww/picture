package com.applory.pictureserver.domain.favorite;

import lombok.Data;

import java.util.UUID;

public class FavoriteDTO {
    @Data
    public static class Toggle {
        private UUID userId;
        private UUID targetUserId;
    }
}
