package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {


    @Query("select r " +
            "from Review r " +
            "where r.seller = :seller " +
            "order by r.createdDt desc ")
    Review findSellersLatestReview(@Param("seller") User seller);

    int countBySeller(User seller);
}
