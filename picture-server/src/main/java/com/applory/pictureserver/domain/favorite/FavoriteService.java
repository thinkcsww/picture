package com.applory.pictureserver.domain.favorite;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.domain.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public void toggleFavorite(FavoriteDTO.Toggle body) {
        User currentUser = userRepository.getById(body.getUserId());

        Optional<Favorite> optionalFavorite = favoriteRepository.findByUser_IdAndTargetUser_id(body.getUserId(), body.getTargetUserId());

        if (optionalFavorite.isPresent()) {
            favoriteRepository.delete(optionalFavorite.get());
        } else {
            Favorite newFavorite = Favorite.builder()
                    .user(currentUser)
                    .targetUser(userRepository.getById(body.getTargetUserId()))
                    .build();

            favoriteRepository.save(newFavorite);
        }
    }
}
