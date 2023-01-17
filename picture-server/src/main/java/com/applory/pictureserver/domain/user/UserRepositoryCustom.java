package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.user.querydto.SellerListVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryCustom {
    Page<User> findClientUserBySearch(UserDto.SearchClient search, Pageable pageable);

    Page<SellerListVM> findSellerUserBySearch(UserDto.SearchSeller search, Pageable pageable);
}
