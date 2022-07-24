package com.applory.pictureserver;

import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.request.Request;
import com.applory.pictureserver.domain.request.RequestDto;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserDto;

import java.time.LocalDateTime;

public class TestUtil {
    public static UserDto.Create createValidClientUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setPassword(username + "durtnlchrhtn@1");
        user.setNickname("test-nickname");
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
        user.setSnsType(User.SnsType.KAKAO);
        user.setPeoplePrice(2000);
        user.setBackgroundPrice(2000);
        user.setOfficialPrice(2000);

        return user;
    }

    public static AuthDto.Login createValidLoginDto(String username) {
        AuthDto.Login login = new AuthDto.Login();
        login.setUsername(username);
        login.setKakaoToken("test");

        return login;
    }

    public static RequestDto.Create createValidRequestDto() {
        RequestDto.Create dto = new RequestDto.Create();
        dto.setRequestType(Request.RequestType.BACKGROUND);
        dto.setDesiredPrice(2000);
        dto.setDescription("설명입니다");
        dto.setTitle("제목입니다");
        dto.setDueDate(LocalDateTime.of(2022, 12, 25, 23, 59));
        dto.setMatchYn("N");
        dto.setCompleteYn("N");

        return dto;
    }
}
