package com.applory.pictureserver.domain.matching;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchingDto {

    @Getter
    @Setter
    @Builder
    public static class Search {
        public String sellerEnabledYn;
        public UUID userId;
        public String completeYn;
    }

    @Getter
    @Setter
    public static class VM {
        String opponentNickname;
        String comment;
        UUID matchingId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime dueDate;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime completeDt;
        Matching.Status status;

        public VM(Matching matching, String isSellerYn) {
            if ("Y".equals(isSellerYn)) {
                this.opponentNickname = matching.getClient().getNickname();
            } else {
                this.opponentNickname = matching.getSeller().getNickname();
            }
            this.comment = matching.getComment();
            this.matchingId = matching.getId();
            this.dueDate = matching.getDueDate();
            this.status = matching.getStatus();
            this.completeDt = matching.getCompleteDt();
        }
    }
}
