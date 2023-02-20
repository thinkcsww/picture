package com.applory.pictureserver.domain.review;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String>, ReviewRepositoryCustom {

    List<Review> findBySellerOrderByCreatedDtDesc(User seller);

}
