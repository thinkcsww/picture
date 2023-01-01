package com.applory.pictureserver.domain.matching;

import com.applory.pictureserver.domain.request.Request;
import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.shared.Constant;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "MATCHING")
public class Matching {

    public enum Status {
        REQUEST,
        ACCEPT,
        DECLINE
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_ID")
    private User seller;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENT_ID")
    private User client;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    private Request request;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SPECIALTY")
    private Constant.Specialty specialty;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "DUE_DATE")
    private LocalDateTime dueDate;

    @Column(name = "COMPLETE_YN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String completeYN;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "COMMENT")
    private String comment;

    @Builder
    public Matching(User seller, User client, Request request, Constant.Specialty specialty, Integer price, LocalDateTime dueDate, String completeYN, Status status, String comment) {
        this.seller = seller;
        this.client = client;
        this.request = request;
        this.specialty = specialty;
        this.price = price;
        this.dueDate = dueDate;
        this.completeYN = completeYN;
        this.status = status;
        this.comment = comment;
    }
}
