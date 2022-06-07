package com.applory.pictureserver.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class UserDto {

    @Getter
    @Setter
    public static class UserVM {
        private UUID id;

        public UserVM(User user) {
            this.id = user.getId();
        }
    }

    @Getter
    @Setter
    public static class Create {
        private String username;

        private String password;
    }


}
