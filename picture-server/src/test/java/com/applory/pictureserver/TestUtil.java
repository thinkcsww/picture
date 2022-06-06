package com.applory.pictureserver;

import com.applory.pictureserver.domain.oauth.LoginDto;
import com.applory.pictureserver.domain.user.UserDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
    public static UserDto.Create createValidUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setPassword(username + "durtnlchrhtn@1");

        return user;
    }

    public static LoginDto.Login createValidLoginDto(String username) {
        LoginDto.Login login = new LoginDto.Login();
        login.setUsername(username);
        login.setKakaoToken("test");

        return login;
    }
}
