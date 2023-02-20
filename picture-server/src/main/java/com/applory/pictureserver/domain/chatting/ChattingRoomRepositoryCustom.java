package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;

import java.util.List;
import java.util.UUID;

public interface ChattingRoomRepositoryCustom {
    List<ChattingRoom> findAllByUser(User user);

    List<ChattingRoom> findAllByRoomIds(List<String> roomIds);
}
