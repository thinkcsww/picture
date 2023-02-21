package com.applory.pictureserver.domain.chatting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, String>, ChattingMessageRepositoryCustom {
    List<ChattingMessage> findByChattingRoom_Id(String roomId);

    ChattingMessage findTopByChattingRoomOrderByCreatedDtDesc(ChattingRoom chattingRoom);

    ChattingMessage findTopByChattingRoomIdAndMessageTypeOrderByCreatedDtDesc(String roomId, ChattingMessage.Type type);

}
