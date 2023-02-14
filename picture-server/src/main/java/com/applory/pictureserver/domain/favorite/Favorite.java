package com.applory.pictureserver.domain.favorite;

import com.applory.pictureserver.domain.user.User;
import com.applory.pictureserver.shared.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "FAVORITE")
public class Favorite extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(100)")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_USER_ID")
    private User targetUser;

    @Builder
    public Favorite(User user, User targetUser) {
        this.user = user;
        this.targetUser = targetUser;
    }
}
