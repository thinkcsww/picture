package com.applory.pictureserver.domain.request;

import com.applory.pictureserver.shared.BaseTimeEntity;
import com.applory.pictureserver.shared.Constant;
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

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "USER_ID")
    User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SPECIALTY")
    private Constant.Specialty specialty;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESIRED_PRICE")
    private Integer desiredPrice;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "READ_COUNT")
    private Integer readCount;

    @Column(name = "MATCH_YN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String matchYN;

    @Column(name = "COMPLETE_YN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String completeYN;

}
