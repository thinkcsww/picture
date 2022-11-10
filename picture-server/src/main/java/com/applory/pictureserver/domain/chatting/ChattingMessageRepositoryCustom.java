package com.applory.pictureserver.domain.chatting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ChattingMessageRepositoryCustom {

    int countUnreadMessageOfRoom(UUID roomId, UUID userId);

    Page<ChattingMessage> findByChattingRoomId(UUID roomId, String userId, Pageable pageable);
}
