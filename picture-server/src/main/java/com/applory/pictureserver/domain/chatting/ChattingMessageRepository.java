package com.applory.pictureserver.domain.chatting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, UUID>, ChattingMessageRepositoryCustom {
    List<ChattingMessage> findByChattingRoom_Id(UUID roomId);

    ChattingMessage findTopByChattingRoom(ChattingRoom chattingRoom);

}
