package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.config.AppConfiguration;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingDto;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.exception.BadRequestException;
import com.applory.pictureserver.exception.NotFoundException;
import com.applory.pictureserver.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AppConfiguration appConfiguration;

    private final MatchingRepository matchingRepository;

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

    public Page<User> getSellerUsers(UserDto.SearchSeller search, Pageable pageable) {
        return userRepository.findSellerUserBySearch(search, pageable);
    }

    public Page<User> getClientUsers(UserDto.SearchClient search, Pageable pageable) {
        return userRepository.findClientUserBySearch(search, pageable);
    }

    public UserDto.VM getUserMe(MatchingDto.Search search) {
        String username = SecurityUtils.getPrincipal();

        User findUser = userRepository.findByUsername(username);
        search.setUserId(findUser.getId());
        search.setCompleteYn("N");

        List<MatchingDto.VM> matchings = matchingRepository.findBySearch(search)
                .stream()
                .map((matching) -> new MatchingDto.VM(matching, findUser.getSellerEnabledYn()))
                .collect(Collectors.toList());

        UserDto.VM vm = new UserDto.VM(findUser);
        vm.setMatchings(matchings);

        return vm;
    }

    public void checkNickname(String nickname) {
        User userInDB = userRepository.findByNickname(nickname);

        if (userInDB != null) {
            throw new BadRequestException(nickname + " is already in use");
        }
    }

    public UserDto.SellerVM getSellerUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDto.SellerVM sellerVM = new UserDto.SellerVM(user);

            return sellerVM;
        } else {
            throw new NotFoundException(id + " user is not found");
        }

    }
}
