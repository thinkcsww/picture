package com.applory.pictureserver.domain.favorite;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    Optional<Favorite> findByUser_IdAndTargetUser_id(String userId, String targetUserId);

    List<Favorite> findByUser(User user);
}
