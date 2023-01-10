package com.applory.pictureserver.domain.matching;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MatchingRepository extends JpaRepository<Matching, UUID>, MatchingRepositoryCustom {
    Optional<Matching> findBySellerAndClientAndCompleteYN(User seller, User client, String completeYN);
    Optional<Matching> findBySeller_IdAndClient_IdAndCompleteYN(UUID sellerId, UUID clientId, String completeYN);

}
