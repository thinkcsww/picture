package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.config.AppConfiguration;
import com.applory.pictureserver.domain.favorite.Favorite;
import com.applory.pictureserver.domain.favorite.FavoriteRepository;
import com.applory.pictureserver.domain.file.File;
import com.applory.pictureserver.domain.file.FileService;
import com.applory.pictureserver.domain.matching.Matching;
import com.applory.pictureserver.domain.matching.MatchingDto;
import com.applory.pictureserver.domain.matching.MatchingRepository;
import com.applory.pictureserver.domain.review.Review;
import com.applory.pictureserver.domain.review.ReviewDTO;
import com.applory.pictureserver.domain.review.ReviewRepository;
import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import com.applory.pictureserver.shared.Constant;
import com.applory.pictureserver.shared.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final AppConfiguration appConfiguration;

    private final MatchingRepository matchingRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ReviewRepository reviewRepository;

    private final FavoriteRepository favoriteRepository;

    private final FileService fileService;

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
                throw new IllegalStateException("fromDt is bigger than toDt");
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
    @Transactional(readOnly = true)
    public Page<SellerListVM> getSellerUsers(UserDto.SearchSeller search, Pageable pageable) {
        return userRepository.findSellerUserBySearch(search, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> getClientUsers(UserDto.SearchClient search, Pageable pageable) {
        return userRepository.findClientUserBySearch(search, pageable);
    }

    @Transactional(readOnly = true)
    public UserDto.VM getUserMe() {
        User findUser = userRepository.findByUsername(SecurityUtils.getPrincipal());

        MatchingDto.Search matchingSearch = MatchingDto.Search.builder()
                .userId(findUser.getId())
                .sellerEnabledYn(findUser.getSellerEnabledYn())
                .build();

        Map<Matching.Status, List<MatchingDto.VM>> matchings = matchingRepository.findBySearch(matchingSearch)
                .stream()
                .map((matching) -> new MatchingDto.VM(matching, findUser.getSellerEnabledYn()))
                .collect(Collectors.groupingBy(MatchingDto.VM::getStatus));

        UserDto.VM vm = new UserDto.VM(findUser);
        vm.setMatchings(matchings);

        return vm;
    }

    @Transactional(readOnly = true)
    public void checkNickname(String nickname) {
        User userInDB = userRepository.findByNickname(nickname);

        if (userInDB != null) {
            throw new IllegalStateException(nickname + " is already in use");
        }
    }

    @Transactional(readOnly = true)
    public UserDto.SellerVM getSellerDetail(UUID id) {
        User seller = userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Seller: " + id + " not exist"));
        List<Review> reviews = reviewRepository.findBySellerOrderByCreatedDtDesc(seller);

        List<Matching> matchings = matchingRepository.findBySellerAndCompleteYN(seller, "Y");

        Map<Constant.Specialty, Long> matchingCountBySpecialty = matchings
                .stream()
                .collect(Collectors.groupingBy(Matching::getSpecialty, Collectors.counting()));

        Map<Integer, Long> reviewCountByRating = reviews
                .stream()
                .collect(Collectors.groupingBy(Review::getRate, Collectors.counting()));


        UserDto.SellerVM sellerVM = new UserDto.SellerVM(seller);
        sellerVM.setLatestReview(!CollectionUtils.isEmpty(reviews) ? new ReviewDTO.ReviewVM(reviews.get(0)) : null);
        sellerVM.setReviewCnt(reviews.size());
        sellerVM.setRateAvg(reviews.stream().collect(Collectors.averagingInt(Review::getRate)));
        sellerVM.setMatchingCountBySpecialty(matchingCountBySpecialty);
        sellerVM.setCompleteMatchingCnt(matchings.size());
        sellerVM.setReviewCountByRating(reviewCountByRating);

        String username = SecurityUtils.getPrincipal();
        if (Objects.nonNull(username)) {
            Optional<Favorite> favoriteOptional = favoriteRepository.findByUser_IdAndTargetUser_id(userRepository.findByUsername(username).getId(), id);
            sellerVM.setFavorite(favoriteOptional.isPresent());
        }

        return sellerVM;
    }

    public void updateProfileImage(UUID userId, UserDto.UpdateProfileImage dto) {
        User userInDB = userRepository.getById(userId);

        // 기존 파일 삭제
        if (Objects.nonNull(userInDB.getFile())) {
            fileService.deleteFile(userInDB.getFile().getId());
        }

        try {
            File fileInDB = fileService.storeFile(dto.getAttachFile());
            userInDB.setFile(fileInDB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
