package com.applory.pictureserverkt.user

import com.applory.pictureserverkt.shared.BaseTimeEntity
import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
@Table(name = "USER")
class User(username: String, password: String): BaseTimeEntity(), UserDetails {

    enum class SnsType {
        KAKAO, APPLE
    }

    enum class SellerSpecialty {
        OFFICIAL, PEOPLE, BACKGROUND
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @Column(name = "USER_NAME")
    @NotEmpty
    private val username: String = username


    @Column(name = "PASSWORD")
    @NotEmpty
    private val password: String = password

    @Column(name = "nickname", length = 20)
    @NotEmpty
    var nickname:  String? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "sellerEnabledYn", length = 1, columnDefinition = "varchar(1) default 'N'")
    var sellerEnabledYn: String? = null

    @Column(name = "workHourFromDt")
    var workHourFromDt: Int? = null

    @Column(name = "workHourToDt")
    var workHourToDt: Int? = null

    // people, bg, official
    @Column(name = "specialty")
    var specialty: String? = null

    @Column(name = "useTermAgreeYn", length = 1, columnDefinition = "varchar(1) default 'Y'")
    @NotEmpty
    var useTermAgreeYn: String? = null

    @Column(name = "personalInfoUseTermAgreeYn", length = 1, columnDefinition = "varchar(1) default 'Y'")
    @NotEmpty
    var personalInfoUseTermAgreeYn: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "snsType")
    var snsType: SnsType? = null

    override fun getUsername(): String {
        return username
    }

    override fun getPassword(): String {
        return password
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return AuthorityUtils.createAuthorityList("ROLE_USER")
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}
