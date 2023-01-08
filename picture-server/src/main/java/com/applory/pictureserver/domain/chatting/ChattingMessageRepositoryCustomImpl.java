package com.applory.pictureserver.domain.chatting;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static com.applory.pictureserver.domain.chatting.QChattingMessage.chattingMessage;

@RequiredArgsConstructor
public class ChattingMessageRepositoryCustomImpl implements ChattingMessageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int countUnreadMessageOfRoom(UUID roomId, UUID userId) {
        JPQLQuery<ChattingMessage> query = jpaQueryFactory
                .selectFrom(chattingMessage)
                .where(chattingMessage.chattingRoom.id.eq(roomId)
                        .and(chattingMessage.sender.id.ne(userId))
                        .and(chattingMessage.readBy.notLike("%" + userId + "%").or(chattingMessage.readBy.isNull()))
                        .and(chattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(chattingMessage.visibleTo.eq(userId.toString()))));
        return (int)query.fetchCount();
    }

    @Override
    public Page<ChattingMessage> findByChattingRoomId(UUID roomId, String userId, Pageable pageable) {
        JPQLQuery<ChattingMessage> query = jpaQueryFactory
                .selectFrom(chattingMessage)
                .where(chattingMessage.chattingRoom.id.eq(roomId)
                        .and(chattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(chattingMessage.visibleTo.eq(userId.toString()))))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<ChattingMessage> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());

    }
}
