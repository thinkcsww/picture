package com.applory.pictureserver;

import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserDto;

public class TestUtil {
    public static UserDto.Create createValidClientUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setPassword(username + "durtnlchrhtn@1");
        user.setNickname("test-nickname");
        user.setUseTermAgreeYN("Y");
        user.setPersonalInfoUseTermAgreeYn("Y");
        user.setSnsType(User.SnsType.KAKAO);

        return user;
    }

    public static UserDto.Create createValidSellerUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setPassword(username + "durtnlchrhtn@1");
        user.setNickname("test-nickname");
        user.setDescription("test-description");
        user.setSellerEnabledYn("Y");
        user.setWorkHourFromDt(1700);
        user.setWorkHourToDt(1830);
        user.setSpecialty(User.SellerSpecialty.PEOPLE.toString());
        user.setUseTermAgreeYN("Y");
        user.setPersonalInfoUseTermAgreeYn("Y");
        user.setSnsType(User.SnsType.KAKAO);

        return user;
    }

    public static AuthDto.Login createValidLoginDto(String username) {
        AuthDto.Login login = new AuthDto.Login();
        login.setUsername(username);
        login.setKakaoToken("test");

        return login;
    }
}
