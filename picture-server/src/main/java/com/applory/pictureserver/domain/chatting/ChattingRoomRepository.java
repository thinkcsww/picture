package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, UUID> {
    Page<ChattingRoom> findAllByChattingRoomMembers_User(User user, Pageable pageable);
}
