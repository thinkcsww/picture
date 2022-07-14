package com.applory.pictureserver.domain.chatting;

import java.util.UUID;

public interface ChattingMessageRepositoryCustom {

    int countUnreadMessageOfRoom(UUID roomId, UUID userId);
}
