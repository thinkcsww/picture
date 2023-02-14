package com.applory.pictureserver.domain.favorite;

import com.applory.pictureserver.TestConstants;
import com.applory.pictureserver.TestUtil;
import com.applory.pictureserver.config.WithMockClientLogin;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.applory.pictureserver.shared.Constant.Specialty.PEOPLE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class FavoriteServiceTest {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User seller = userRepository.save(TestUtil.createSeller(TestConstants.TEST_SELLER_NICKNAME, PEOPLE));
        User client = userRepository.save(TestUtil.createClient());
    }

    @DisplayName("Toggle Favorite - 단골로 등록할 때")
    @Test
    @WithMockClientLogin
    public void toggleFavorite_save() {
        User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
        favoriteService.toggleFavorite(seller.getId());

        assertThat(favoriteRepository.findAll().size()).isEqualTo(1);
    }

    @DisplayName("Toggle Favorite - 단골 취소할 때")
    @Test
    @WithMockClientLogin
    public void toggleFavorite_delete() {
        User seller = userRepository.findByUsername(TestConstants.TEST_SELLER_USERNAME);
        favoriteService.toggleFavorite(seller.getId());
        favoriteService.toggleFavorite(seller.getId());

        assertThat(favoriteRepository.findAll().size()).isEqualTo(0);
    }
}
