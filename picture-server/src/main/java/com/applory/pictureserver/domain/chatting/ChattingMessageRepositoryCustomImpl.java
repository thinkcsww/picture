package com.applory.pictureserver.domain.chatting;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.UUID;

public class ChattingMessageRepositoryCustomImpl extends QuerydslRepositorySupport implements ChattingMessageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public ChattingMessageRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        super(ChattingMessage.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }


    @Override
    public int countUnreadMessageOfRoom(UUID roomId, UUID userId) {
        QChattingMessage qChattingMessage = QChattingMessage.chattingMessage;
        JPQLQuery<ChattingMessage> query = jpaQueryFactory.select(qChattingMessage)
                .from(qChattingMessage)
                .where(qChattingMessage.chattingRoom.id.eq(roomId)
                        .and(qChattingMessage.sender.id.ne(userId))
                        .and(qChattingMessage.readBy.notLike("%" + userId + "%").or(qChattingMessage.readBy.isNull()))
                        .and(qChattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(qChattingMessage.visibleTo.eq(userId.toString()))));
        return (int)query.fetchCount();
    }

    @Override
    public Page<ChattingMessage> findByChattingRoomId(UUID roomId, String userId, Pageable pageable) {
        QChattingMessage qChattingMessage = QChattingMessage.chattingMessage;
        JPQLQuery<ChattingMessage> query = jpaQueryFactory.select(qChattingMessage)
                .from(qChattingMessage)
                .where(qChattingMessage.chattingRoom.id.eq(roomId)
                        .and(qChattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(qChattingMessage.visibleTo.eq(userId.toString()))));

        query = getQuerydsl().applyPagination(pageable, query);

        QueryResults<ChattingMessage> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());

    }
}
