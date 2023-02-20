package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.applory.pictureserver.domain.chatting.QChattingRoom.chattingRoom;
import static com.applory.pictureserver.domain.chatting.QChattingRoomMember.chattingRoomMember;
import static com.applory.pictureserver.domain.user.QUser.user;

@RequiredArgsConstructor
public class ChattingRoomRepositoryCustomImpl implements ChattingRoomRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<ChattingRoom> findAllByUser(User user) {
        return jpaQueryFactory.select(chattingRoom)
                .from(chattingRoom)
                .where(chattingRoom.clientId.eq(user.getId())
                        .or(chattingRoom.sellerId.eq(user.getId())))
                .fetch();
    }

    @Override
    public List<ChattingRoom> findAllByRoomIds(List<String> roomIds) {
        return jpaQueryFactory.select(chattingRoom)
                .from(chattingRoom)
                .join(chattingRoom.chattingRoomMembers, chattingRoomMember).fetchJoin()
                .join(chattingRoomMember.user, user).fetchJoin()
                .where(chattingRoom.id.in(roomIds))
                .distinct()
                .fetch();
    }
}
