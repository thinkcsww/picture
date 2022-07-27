package com.applory.pictureserver.domain.oauth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

public class AuthDto {

    @Getter
    @Setter
    public static class Login {

        @NotNull
        private String username;

        @NotNull
        private String token;
    }

    @Getter
    @Setter
    public static class RefreshToken {
        @NotNull
        private String refreshToken;
    }
}
