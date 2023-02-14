package com.applory.pictureserver.domain.favorite;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import com.applory.pictureserver.shared.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public void toggleFavorite(UUID targetUserId) {
        String username = SecurityUtils.getPrincipal();
        User currentUser = userRepository.findByUsername(username);

        Optional<Favorite> optionalFavorite = favoriteRepository.findByUser_IdAndTargetUser_id(currentUser.getId(), targetUserId);

        if (optionalFavorite.isPresent()) {
            favoriteRepository.delete(optionalFavorite.get());
        } else {
            Favorite newFavorite = Favorite.builder()
                    .user(currentUser)
                    .targetUser(userRepository.getById(targetUserId))
                    .build();

            favoriteRepository.save(newFavorite);
        }
    }
}
