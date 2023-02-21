package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.favorite.FavoriteDTO;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingDto;
import com.applory.pictureserver.domain.review.ReviewDTO;
import com.applory.pictureserver.shared.Constant;
import lombok.*;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VM {
        private String id;
        private String username;
        private String nickname;
        private User.SnsType snsType;
        private String description;
        private Integer workHourFromDt;
        private Integer workHourToDt;
        private String specialty;
        private String sellerEnabledYN;
        private LocalDateTime createdDt;
        private LocalDateTime updatedDt;
        private Integer peoplePrice;
        private Integer backgroundPrice;
        private Integer officialPrice;
        private String fileName;

        private Map<Matching.Status, List<MatchingDto.VM>> matchings;
        private List<FavoriteDTO.VM> favoriteUsers;

        public VM(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.snsType = user.getSnsType();
            this.description = user.getDescription();
            this.workHourFromDt = user.getWorkHourFromDt();
            this.workHourToDt = user.getWorkHourToDt();
            this.specialty = user.getSpecialty();
            this.sellerEnabledYN = user.getSellerEnabledYn();
            this.createdDt = user.getCreatedDt();
            this.updatedDt = user.getUpdatedDt();
            this.peoplePrice = user.getPeoplePrice();
            this.backgroundPrice = user.getBackgroundPrice();
            this.officialPrice = user.getOfficialPrice();

            if (Objects.nonNull(user.getFile())){
                this.fileName = user.getFile().getStoreFileName();
            }
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SellerVM {
        private String id;
        private String username;
        private String nickname;
        private User.SnsType snsType;
        private String description;
        private Integer workHourFromDt;
        private Integer workHourToDt;
        private String specialty;
        private String sellerEnabledYN;
        private LocalDateTime createdDt;
        private LocalDateTime updatedDt;
        private Integer peoplePrice;
        private Integer backgroundPrice;
        private Integer officialPrice;
        private ReviewDTO.ReviewVM latestReview;
        private Map<Constant.Specialty, Long> matchingCountBySpecialty;
        private Map<Integer, Long> reviewCountByRating;
        private String fileName;
        private boolean isFavorite;

        private double rateAvg;

        private int closingRate;

        private long reviewCnt;

        private long completeMatchingCnt;

        public SellerVM(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.snsType = user.getSnsType();
            this.description = user.getDescription();
            this.workHourFromDt = user.getWorkHourFromDt();
            this.workHourToDt = user.getWorkHourToDt();
            this.specialty = user.getSpecialty();
            this.sellerEnabledYN = user.getSellerEnabledYn();
            this.createdDt = user.getCreatedDt();
            this.updatedDt = user.getUpdatedDt();
            this.peoplePrice = user.getPeoplePrice();
            this.backgroundPrice = user.getBackgroundPrice();
            this.officialPrice = user.getOfficialPrice();

            if (Objects.nonNull(user.getFile())) {
                this.fileName = user.getFile().getStoreFileName();
            }
        }
    }

    @Getter
    @Setter
    public static class Create {
        @NotEmpty
        private String username;
        @NotEmpty
        @UniqueNickname
        private String nickname;
        @NotNull
        private User.SnsType snsType;
        private String description;
        private String sellerEnabledYN;
        private Integer workHourFromDt;
        private Integer workHourToDt;
        private String specialty;
        private Integer peoplePrice;
        private Integer backgroundPrice;
        private Integer officialPrice;
    }

    @Getter
    @Setter
    public static class SearchSeller {
        @Min(0)
        @Max(2400)
        @Nullable
        private String currentTime;

        private Constant.Specialty specialty;

        private String nickname;

    }

    @Getter
    @Setter
    public static class SearchClient {

    }

    @Data
    public static class UpdateProfileImage {
        private MultipartFile attachFile;
    }

    @Getter
    @Setter
    @Builder
    public static class Search {
        public String sellerEnabledYn;
        public String userId;
        public String completeYn;
    }


}
