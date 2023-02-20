package com.applory.pictureserver.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String>, UserRepositoryCustom {

    User findByUsername(String username);

    User findByNickname(String nickname);
}
