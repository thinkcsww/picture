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
@Table(name = "user")
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
    @Column(name = "username")
    private String username;

    @NotEmpty
    @Column(name = "password")
    private String password;

    @NotEmpty
    @Column(name = "nickname", length = 20)
    private String nickname;

    @Column(name = "description")
    private String description;

    @Column(name = "sellerEnabledYn", length = 1, columnDefinition = "varchar(1) default 'N'")
    private String sellerEnabledYn;

    @Column(name = "workHourFromDt")
    private Integer workHourFromDt;

    @Column(name = "workHourToDt")
    private Integer workHourToDt;

    // people, bg, official
    @Column(name = "specialty")
    private String specialty;

    @NotEmpty
    @Column(name = "useTermAgreeYn", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String useTermAgreeYn;

    @NotEmpty
    @Column(name = "personalInfoUseTermAgreeYn", length = 1, columnDefinition = "varchar(1) default 'Y'")
    private String personalInfoUseTermAgreeYn;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "snsType")
    private SnsType snsType;

//    @Column(name = "profileFileGroupId")
//    private String profileFileGroupId;

}
