package com.applory.pictureserver.domain.matching;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatchingRepository extends JpaRepository<Matching, UUID> {

    Matching findBySellerAndClientAndCompleteYN(User seller, User client, String completeYN);
}
