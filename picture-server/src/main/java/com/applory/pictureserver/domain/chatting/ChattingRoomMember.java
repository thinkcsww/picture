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

    @Column(name = "USE_YN", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String useYN;

    @ManyToOne
    @JoinColumn(name="CHATTING_ROOM_ID")
    private ChattingRoom chattingRoom;

    @OneToOne
    private User user;
}
