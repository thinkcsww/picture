package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_MESSAGE")
public class ChattingMessage {

    public enum VisibleToType {
        NONE,
        ALL
    }
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "CHATTING_ROOM_ID")
    private ChattingRoom chattingRoom;

    @NotEmpty
    @Column(name = "MESSAGE", length = 20)
    private String message;

    @NotNull
    @OneToOne
    private User sender;

    @NotNull
    @OneToOne
    private User receiver;

    // ALL, NONE, UserId - UserId일 경우 저장된 userId의 소유자만 읽을 수 있다
    @Column(name = "VISIBLE_TO", columnDefinition = "varchar(50) default 'ALL'")
    private String visibleTo;

}
