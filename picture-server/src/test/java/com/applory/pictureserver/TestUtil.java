package com.applory.pictureserver;

import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.oauth.AuthDto;
import com.applory.pictureserver.domain.request.Request;
import com.applory.pictureserver.domain.request.RequestDto;
import com.applory.pictureserver.domain.review.Review;
import com.applory.pictureserver.shared.Constant;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserDto;

import java.time.LocalDateTime;

public class TestUtil {

    public static User createSeller(String nickname, Constant.Specialty specialty) {
        User sellerUser = new User();
        sellerUser.setPassword("password");
        sellerUser.setNickname(nickname);
        sellerUser.setUsername(TestConstants.TEST_SELLER_USERNAME);
        sellerUser.setOfficialPrice(2000);
        sellerUser.setBackgroundPrice(2000);
        sellerUser.setPeoplePrice(2000);
        sellerUser.setDescription("셀러 설명입니다.");
        sellerUser.setSnsType(User.SnsType.KAKAO);
        sellerUser.setSpecialty(specialty.toString());
        sellerUser.setSellerEnabledYn("Y");
        sellerUser.setWorkHourFromDt(1110);
        sellerUser.setWorkHourToDt(1810);

        return sellerUser;
    }

    public static User createClient() {
        User clientUser = new User();
        clientUser.setNickname(TestConstants.TEST_CLIENT_NICKNAME);
        clientUser.setUsername(TestConstants.TEST_CLIENT_USERNAME);
        clientUser.setDescription("클라이언트 설명입니다.");
        clientUser.setSnsType(User.SnsType.KAKAO);
        clientUser.setSellerEnabledYn("N");

        return clientUser;
    }

    public static Review createReview(User seller, User client, int rate) {
        Review review = new Review();
        review.setContent("content");
        review.setSeller(seller);
        review.setClient(client);
        review.setRate(rate);

        return review;
    }

    public static Matching createMatching(User seller, User client, Matching.Status status, Constant.Specialty specialty, String completeYN) {
        Matching matching = new Matching();
        matching.setStatus(status);
        matching.setSeller(seller);
        matching.setClient(client);
        matching.setSpecialty(specialty);
        matching.setDueDate(LocalDateTime.now().plusHours(5));
        matching.setCompleteYN(completeYN);
        matching.setComment("잘 부탁드립니다^^");
        matching.setPrice(2000);

        return matching;
    }


    public static UserDto.Create createValidClientUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setNickname(getNickname());
        user.setSnsType(User.SnsType.KAKAO);

        return user;
    }

    public static UserDto.Create createValidSellerUser(String username) {
        UserDto.Create user = new UserDto.Create();
        user.setUsername(username);
        user.setNickname(getNickname());
        user.setDescription("test-description");
        user.setSellerEnabledYN("Y");
        user.setWorkHourFromDt(1700);
        user.setWorkHourToDt(1830);
        user.setSpecialty(Constant.Specialty.PEOPLE.toString());
        user.setSnsType(User.SnsType.KAKAO);
        user.setPeoplePrice(2000);
        user.setBackgroundPrice(2000);
        user.setOfficialPrice(2000);

        return user;
    }

    private static String getNickname() {
        return "test-nickname" + ((int)Math.floor(Math.random() * (1000000)));
    }

    public static AuthDto.Login createValidLoginDto(String username) {
        AuthDto.Login login = new AuthDto.Login();
        login.setUsername(username);
        login.setToken("test");

        return login;
    }

    public static RequestDto.Create createValidRequestDto() {
        RequestDto.Create dto = new RequestDto.Create();
        dto.setSpecialty(Constant.Specialty.BACKGROUND);
        dto.setDesiredPrice(2000);
        dto.setDescription("설명입니다");
        dto.setTitle("제목입니다");
        dto.setDueDate(LocalDateTime.now().plusHours(24));
        dto.setMatchYn("N");
        dto.setCompleteYn("N");

        return dto;
    }

    public static Request createRequest(User user, LocalDateTime dueDate, String title, String desc, Constant.Specialty specialty, int price) {
        Request create = new Request();
        create.setUser(user);
        create.setTitle(title);
        create.setSpecialty(specialty);
        create.setDueDate(dueDate);
        create.setDescription(desc);
        create.setDesiredPrice(price);
        create.setMatchYN("N");
        create.setChatCount(0);
        return create;
    }

    public static RequestDto.Create createRequestDto(LocalDateTime dueDate, String title, String desc, Constant.Specialty specialty, int price) {
        RequestDto.Create create = new RequestDto.Create();
        create.setTitle(title);
        create.setSpecialty(specialty);
        create.setDueDate(dueDate);
        create.setDescription(desc);
        create.setDesiredPrice(price);
        return create;
    }
}
