package com.applory.pictureserver.domain.matching;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface MatchingRepository extends JpaRepository<Matching, String>, MatchingRepositoryCustom {
    Optional<Matching> findBySellerAndClientAndCompleteYN(User seller, User client, String completeYN);
    Optional<Matching> findBySeller_IdAndClient_IdAndCompleteYN(String sellerId, String clientId, String completeYN);

    List<Matching> findBySellerAndCompleteYN(User seller, String y);
}
