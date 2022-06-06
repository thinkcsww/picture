package com.applory.pictureserver.domain.oauth;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class LoginDto {

    @Getter
    @Setter
    public static class Login {

        @NotNull
        private String username;

        @NotNull
        private String kakaoToken;
    }
}
