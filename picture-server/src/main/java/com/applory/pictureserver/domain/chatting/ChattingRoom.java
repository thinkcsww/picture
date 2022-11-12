package com.applory.pictureserver.domain.chatting;

import com.applory.pictureserver.domain.shared.BaseTimeEntity;
import com.applory.pictureserver.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

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
    @Column(name = "ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "chattingRoom")
    private List<ChattingRoomMember> chattingRoomMembers;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE")
    private Type type;

    @Column(name = "SELLER_ID", columnDefinition = "BINARY(16)")
    private UUID sellerId;

    @Column(name = "CLIENT_ID", columnDefinition = "BINARY(16)")
    private UUID clientId;
}
