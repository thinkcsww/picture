package com.applory.pictureserver.domain.file;

import com.applory.pictureserver.shared.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Getter
@Entity
@Table(name = "FILE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "ID", columnDefinition = "VARCHAR(100)")
    private String id;

    @Column(name = "ORIGIN_FILE_NAME")
    private String originFileName;

    @Column(name = "STORE_FILE_NAME")
    private String storeFileName;

    @Builder
    private File(String originFileName, String storeFileName) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
    }
}
