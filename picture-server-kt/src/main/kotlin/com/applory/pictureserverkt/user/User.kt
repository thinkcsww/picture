package com.applory.pictureserverkt.user

import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.hibernate.annotations.GenericGenerator
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotEmpty

@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "USER")
class User(username: String, password: String) {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID? = null

    @Column(name = "USER_NAME")
    @NotEmpty
    var username: String = username

    @Column(name = "PASSWORD")
    @NotEmpty
    var password: String = password

}
