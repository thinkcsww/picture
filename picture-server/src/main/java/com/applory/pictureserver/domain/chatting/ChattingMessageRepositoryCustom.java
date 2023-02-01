package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ChattingMessageRepositoryCustom {

    int countUnreadMessageOfRoom(UUID roomId, UUID userId);

    Page<ChattingMessage> findMessageBySearchQ(ChattingMessageDto.Search search, Pageable pageable);

    List<ChattingMessage> findOpponentsMessage(ChattingRoom room, User user);
}
