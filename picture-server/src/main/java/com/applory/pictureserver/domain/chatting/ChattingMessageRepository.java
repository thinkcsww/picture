package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChattingMessageRepository extends JpaRepository<ChattingMessage, UUID>, ChattingMessageRepositoryCustom {
    List<ChattingMessage> findByChattingRoom_Id(UUID roomId);

    Page<ChattingMessage> findByChattingRoomIdOrderByCreatedDtDesc(UUID id, Pageable pageable);

    ChattingMessage findTopByChattingRoomOrderByCreatedDtDesc(ChattingRoom chattingRoom);

    List<ChattingMessage> findAllByChattingRoomAndReadByIsNullAndSenderNot(ChattingRoom chattingRoom, User sender);

}
