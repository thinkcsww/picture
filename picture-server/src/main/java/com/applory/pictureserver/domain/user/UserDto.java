package com.applory.pictureserver.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class VM {
        private UUID id;
        private String username;
        private String nickname;
        private User.SnsType snsType;
        private String description;
        private Integer workHourFromDt;
        private Integer workHourToDt;
        private String specialty;
        private String sellerEnabledYn;
        private LocalDateTime createdDt;
        private LocalDateTime updatedDt;

        public VM(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.snsType = user.getSnsType();
            this.description = user.getDescription();
            this.workHourFromDt = user.getWorkHourFromDt();
            this.workHourToDt = user.getWorkHourToDt();
            this.specialty = user.getSpecialty();
            this.sellerEnabledYn = user.getSellerEnabledYn();
            this.createdDt = user.getCreatedDt();
            this.updatedDt = user.getUpdatedDt();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SellerVM {
        private UUID id;
        private String username;
        private String nickname;
        private User.SnsType snsType;
        private String description;
        private Integer workHourFromDt;
        private Integer workHourToDt;
        private String specialty;
        private String sellerEnabledYn;
        private LocalDateTime createdDt;
        private LocalDateTime updatedDt;

        private Integer peoplePrice;
        private Integer backgroundPrice;
        private Integer officialPrice;

        private int rating;

        private int closingRate;

        private int reviewCount;

        public SellerVM(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.snsType = user.getSnsType();
            this.description = user.getDescription();
            this.workHourFromDt = user.getWorkHourFromDt();
            this.workHourToDt = user.getWorkHourToDt();
            this.specialty = user.getSpecialty();
            this.sellerEnabledYn = user.getSellerEnabledYn();
            this.createdDt = user.getCreatedDt();
            this.updatedDt = user.getUpdatedDt();
            this.peoplePrice = user.getPeoplePrice();
            this.backgroundPrice = user.getBackgroundPrice();
            this.officialPrice = user.getOfficialPrice();
        }
    }

    @Getter
    @Setter
    public static class Create {
        @NotEmpty
        private String username;

        @NotEmpty
        private String password;

        @NotEmpty
        @UniqueNickname
        private String nickname;

        @NotNull
        private User.SnsType snsType;

        private String description;

        private String sellerEnabledYn;

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

        private String specialty;

        private String nickname;
    }

    @Getter
    @Setter
    public static class SearchClient {

    }
}
