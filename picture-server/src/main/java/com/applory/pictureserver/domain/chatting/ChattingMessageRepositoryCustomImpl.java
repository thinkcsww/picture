package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static com.applory.pictureserver.domain.chatting.QChattingMessage.chattingMessage;

@RequiredArgsConstructor
public class ChattingMessageRepositoryCustomImpl implements ChattingMessageRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public int countUnreadMessageOfRoom(String roomId, String userId) {
        JPQLQuery<ChattingMessage> query = jpaQueryFactory
                .selectFrom(chattingMessage)
                .where(roomEq(roomId)
                        .and(chattingMessage.sender.id.ne(userId))
                        .and(chattingMessage.readBy.notLike("%" + userId + "%").or(chattingMessage.readBy.isNull()))
                        .and(chattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(chattingMessage.visibleTo.eq(userId.toString()))));
        return (int)query.fetchCount();
    }

    @Override
    public Page<ChattingMessage> findMessageBySearchQ(ChattingMessageDto.Search search, Pageable pageable) {
        JPQLQuery<ChattingMessage> query = jpaQueryFactory
                .selectFrom(chattingMessage)
                .where(roomEq(search.getRoomId())
                        .and(chattingMessage.visibleTo.eq(ChattingMessage.VisibleToType.ALL.toString()).or(chattingMessage.visibleTo.eq(search.getUserId().toString()))))
                .orderBy(chattingMessage.createdDt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        QueryResults<ChattingMessage> result = query.fetchResults();

        return new PageImpl<>(result.getResults(), pageable, result.getTotal());

    }

    @Override
    public List<ChattingMessage> findOpponentsMessage(ChattingRoom room, User userId) {
        JPQLQuery<ChattingMessage> query = jpaQueryFactory
                .selectFrom(chattingMessage)
                .where(roomEq(room.getId()),
                        chattingMessage.sender.ne(userId));

        return query.fetch();
    }

    private BooleanExpression roomEq(String roomId) {
        return chattingMessage.chattingRoom.id.eq(roomId);
    }
}
