package com.applory.pictureserver.domain.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {
    Page<User> findClientUserBySearch(UserDto.SearchClient search, Pageable pageable);

    Page<User> findSellerUserBySearch(UserDto.SearchSeller search, Pageable pageable);
}
