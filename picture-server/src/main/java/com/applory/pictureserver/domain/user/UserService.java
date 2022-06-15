package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDto.Create dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setUseTermAgreeYn("Y");
        user.setPersonalInfoUseTermAgreeYn("Y");
        user.setSellerEnabledYn("N");
        user.setSnsType(dto.getSnsType());

        if (StringUtils.hasLength(dto.getDescription())) {
            user.setDescription(dto.getDescription());
        }

        if (StringUtils.hasLength(dto.getSellerEnabledYn()) && "Y".equals(dto.getSellerEnabledYn())) {
            if (dto.getWorkHourFromDt() > dto.getWorkHourToDt()) {
                throw new BadRequestException("fromDt is bigger than toDt");
            }

            user.setSellerEnabledYn(dto.getSellerEnabledYn());
            user.setWorkHourFromDt(dto.getWorkHourFromDt());
            user.setWorkHourToDt(dto.getWorkHourToDt());
            user.setSpecialty(dto.getSpecialty());
        }

        return userRepository.save(user);
    }

    public Page<User> getSellerUsers(UserDto.SearchSeller search, Pageable pageable) {
        return userRepository.findSellerUserBySearch(search, pageable);
    }

    public Page<User> getClientUsers(UserDto.SearchClient search, Pageable pageable) {
        return userRepository.findClientUserBySearch(search, pageable);
    }

    public User getUserMe(CustomUserDetail user) {
        return null;
    }
}
