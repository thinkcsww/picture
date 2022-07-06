package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_ROOM_MEMBER")
public class ChattingRoomMember {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="chattingRoomId")
    private ChattingRoom chattingRoom;

    @OneToOne
    private User user;
}
