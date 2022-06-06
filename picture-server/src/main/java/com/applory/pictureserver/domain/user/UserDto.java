package com.applory.pictureserver.domain.user;

import lombok.Getter;
import lombok.Setter;

public class UserDto {

    @Getter
    @Setter
    public static class Create {
        private String username;

        private String password;
    }
}
