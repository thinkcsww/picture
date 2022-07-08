package com.applory.pictureserver.domain.user;

import com.applory.pictureserver.domain.shared.BaseTimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "USER")
public class User extends BaseTimeEntity implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList("ROLE_USER");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public enum SnsType {
        KAKAO, APPLE
    }

    public enum SellerSpecialty {
        OFFICIAL, PEOPLE, BACKGROUND
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotEmpty
    @Column(name = "USERNAME")
    private String username;

    @NotEmpty
    @Column(name = "PASSWORD")
    private String password;

    @NotEmpty
    @Column(name = "NICKNAME", length = 20)
    private String nickname;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SELLER_ENABLED_YN", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String sellerEnabledYn;

    @Column(name = "WORK_HOUR_FROM_DT")
    private Integer workHourFromDt;

    @Column(name = "WORK_HOUR_TO_DT")
    private Integer workHourToDt;

    // people, bg, official
    @Column(name = "SPECIALTY")
    private String specialty;

    @NotEmpty
    @Column(name = "USE_TERM_AGREE_YN", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String useTermAgreeYN;

    @NotEmpty
    @Column(name = "PERSONAL_INF_OUSE_TERM_AGREE_YN", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String personalInfoUseTermAgreeYN;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "SNS_TYPE")
    private SnsType snsType;

//    @Column(name = "profileFileGroupId")
//    private String profileFileGroupId;

}
