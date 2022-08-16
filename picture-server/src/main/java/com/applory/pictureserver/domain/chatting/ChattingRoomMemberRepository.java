package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChattingRoomMemberRepository extends JpaRepository<ChattingRoomMember, UUID> {
    List<ChattingRoomMember> findByChattingRoom_Id(UUID roomId);

    List<ChattingRoomMember> findByChattingRoom_IdAndUseYN(UUID roomId, String useYN);

    List<ChattingRoomMember> findByChattingRoom_IdAndUser(UUID roomId, User user);
}
