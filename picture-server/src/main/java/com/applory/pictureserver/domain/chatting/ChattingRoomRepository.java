package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, String>, ChattingRoomRepositoryCustom {
    Page<ChattingRoom> findAllByChattingRoomMembers_User(User user, Pageable pageable);

    Optional<ChattingRoom> findBySellerIdAndClientId(String sellerId, String clientId);


    List<ChattingRoom> findAllByChattingRoomMembers_UserAndChattingRoomMembers_UseYN(User user, String useYN);
}
