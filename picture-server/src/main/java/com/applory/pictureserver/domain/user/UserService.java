package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.config.AppConfiguration;
import com.applory.pictureserver.domain.exception.BadRequestException;
import com.applory.pictureserver.domain.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppConfiguration appConfiguration;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDto.Create dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getUsername() + appConfiguration.getPwSalt()));
        user.setNickname(dto.getNickname());
        user.setUseTermAgreeYN("Y");
        user.setPersonalInfoUseTermAgreeYN("Y");
        user.setAgeOver14AgreeYN("Y");
        user.setSellerEnabledYn("N");
        user.setSnsType(dto.getSnsType());

        if (StringUtils.hasLength(dto.getDescription())) {
            user.setDescription(dto.getDescription());
        }

        if (StringUtils.hasLength(dto.getSellerEnabledYN()) && "Y".equals(dto.getSellerEnabledYN())) {
            if (dto.getWorkHourFromDt() > dto.getWorkHourToDt()) {
                throw new BadRequestException("fromDt is bigger than toDt");
            }

            user.setSellerEnabledYn(dto.getSellerEnabledYN());
            user.setWorkHourFromDt(dto.getWorkHourFromDt());
            user.setWorkHourToDt(dto.getWorkHourToDt());
            user.setSpecialty(dto.getSpecialty());
            user.setPeoplePrice(dto.getPeoplePrice());
            user.setBackgroundPrice(dto.getBackgroundPrice());
            user.setOfficialPrice(dto.getOfficialPrice());
        }

        return userRepository.save(user);
    }

    public Page<UserDto.SellerVM> getSellerUsers(UserDto.SearchSeller search, Pageable pageable) {
        Page<User> sellersInDB = userRepository.findSellerUserBySearch(search, pageable);

        return sellersInDB.map((seller) -> {
            UserDto.SellerVM sellerVM = new UserDto.SellerVM(seller);

            return sellerVM;
        });
    }

    public Page<User> getClientUsers(UserDto.SearchClient search, Pageable pageable) {
        return userRepository.findClientUserBySearch(search, pageable);
    }

    public User getUserMe() {
        String username = SecurityUtils.getPrincipal();

        return userRepository.findByUsername(username);
    }

    public void checkNickname(String nickname) {
        User userInDB = userRepository.findByNickname(nickname);

        if (userInDB != null) {
            throw new BadRequestException(nickname + " is already in use");
        }
    }
}
