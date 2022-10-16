package com.applory.pictureserver.domain.chatting;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
