package com.applory.pictureserver.domain.chatting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, UUID> {
}
