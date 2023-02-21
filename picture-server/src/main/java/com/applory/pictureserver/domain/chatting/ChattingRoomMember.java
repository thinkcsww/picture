package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_ROOM_MEMBER")
public class ChattingRoomMember {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(100)")
    private String id;

    @Column(name = "USE_YN", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String useYN;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CHATTING_ROOM_ID")
    private ChattingRoom chattingRoom;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;
}
