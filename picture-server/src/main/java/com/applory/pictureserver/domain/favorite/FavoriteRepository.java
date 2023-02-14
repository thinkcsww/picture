package com.applory.pictureserver.domain.favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    Optional<Favorite> findByUser_IdAndTargetUser_id(UUID userId, UUID targetUserId);
}
