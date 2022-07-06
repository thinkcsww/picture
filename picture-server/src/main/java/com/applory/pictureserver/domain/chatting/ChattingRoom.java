package com.applory.pictureserver.domain.chatting;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "CHATTING_ROOM")
public class ChattingRoom {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;
}
