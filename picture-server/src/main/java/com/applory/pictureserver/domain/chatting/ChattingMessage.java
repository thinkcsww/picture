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
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chattingRoomId")
    private ChattingRoom chattingRoom;

    @NotEmpty
    @Column(name = "message", length = 20)
    private String message;

    @NotNull
    @OneToOne
    private User sender;

    @OneToOne
    @NotNull
    private User receiver;

}
