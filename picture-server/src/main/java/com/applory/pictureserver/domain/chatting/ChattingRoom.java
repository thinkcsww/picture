package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.shared.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_ROOM")
public class ChattingRoom extends BaseTimeEntity {
    public enum Type {
        PRIVATE,
        GROUP
    }

    @Id
    @Column(name = "ID", columnDefinition = "VARCHAR(100)")
    private String id;

    @OneToMany(mappedBy = "chattingRoom", fetch = FetchType.LAZY)
    private List<ChattingRoomMember> chattingRoomMembers;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private Type type;

    @Column(name = "SELLER_ID", columnDefinition = "VARCHAR(100)")
    private String sellerId;

    @Column(name = "CLIENT_ID", columnDefinition = "VARCHAR(100)")
    private String clientId;
}
