package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findBySellerOrderByCreatedDt(User seller);
}
