package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.domain.shared.BaseTimeEntity;
import com.applory.pictureserver.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "REQUEST")
public class Request extends BaseTimeEntity {

    public enum RequestType {
        OFFICIAL, PEOPLE, BACKGROUND, MIX
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "requestType")
    private RequestType requestType;

    @Column(name = "title")
    private String title;

    @Column(name = "desiredPrice")
    private Integer desiredPrice;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "description")
    private String description;

    @Column(name = "readCount")
    private Integer readCount;

    @Column(name = "matchYN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String matchYN;

    @Column(name = "completeYN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String completeYN;

}
