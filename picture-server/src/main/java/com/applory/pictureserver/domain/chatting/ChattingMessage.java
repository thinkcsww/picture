package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.file.File;
import com.applory.pictureserver.shared.BaseTimeEntity;
import com.applory.pictureserver.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_MESSAGE")
public class ChattingMessage extends BaseTimeEntity {

    public enum VisibleToType {
        NONE,
        ALL
    }

    public enum Type {
        ENTER,
        MESSAGE,
        IMAGE,
        RECEIVE,
        REQUEST_MATCHING,
        ACCEPT_MATCHING,
        DECLINE_MATCHING,
        COMPLETE_MATCHING,
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(100)")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHATTING_ROOM_ID")
    private ChattingRoom chattingRoom;

    @NotEmpty
    @Column(name = "MESSAGE", length = 255)
    private String message;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    private User sender;

    // ALL, NONE, UserId - UserId일 경우 저장된 userId의 소유자만 읽을 수 있다
    @Column(name = "VISIBLE_TO", columnDefinition = "varchar(50) default 'ALL'")
    private String visibleTo;

    @Column(name ="READ_BY")
    private String readBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    private File file;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "MESSAGE_TYPE")
    private ChattingMessage.Type messageType;

}
