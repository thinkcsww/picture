package com.applory.pictureserver;

import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.user.UserDto;

public class TestUtil {
    public static UserDto.Create createValidUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setPassword(username + "durtnlchrhtn@1");

        return user;
    }

    public static AuthDto.Login createValidLoginDto(String username) {
        AuthDto.Login login = new AuthDto.Login();
        login.setUsername(username);
        login.setKakaoToken("test");

        return login;
    }
}
