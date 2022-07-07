package com.applory.pictureserverkt.request

import com.applory.pictureserverkt.shared.BaseTimeEntity
import com.applory.pictureserverkt.user.User
import org.hibernate.annotations.GenericGenerator
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "REQUEST")
class Request: BaseTimeEntity() {

    enum class RequestType {
        OFFICIAL, PEOPLE, BACKGROUND, MIX
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @ManyToOne
    @JoinColumn(name = "userId")
    var user: User? = null

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "requestType")
    var requestType: RequestType? = null

    @Column(name = "title")
    var title: String? = null

    @Column(name = "desiredPrice")
    var desiredPrice: Int? = null

    @Column(name = "dueDate")
    var dueDate: LocalDateTime? = null

    @Column(name = "description")
    var description: String? = null

    @Column(name = "readCount")
    var readCount: Int? = null

    @Column(name = "matchYN", length = 1, columnDefinition = "varchar(1) default 'N'")
    var matchYN: String? = null

    @Column(name = "completeYN", length = 1, columnDefinition = "varchar(1) default 'N'")
    var completeYN: String? = null
}
