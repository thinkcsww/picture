package com.applory.pictureserver.service;

import com.applory.pictureserver.domain.file.FileService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @DisplayName("파일 byte[]로 읽어오기 - 성공")
    @Test
    public void getImage() throws IOException {
        Object file = fileService.getFile("f65c4613-e8f7-4e46-b836-3482b9729b5f.jpg");

        assertThat(file).isInstanceOf(byte[].class);
    }

    @DisplayName("없는 파일 조회시 실패")
    @Test
    public void getImage_notExistFile_fail() {
        Assertions.assertThatThrownBy(() -> fileService.getFile("f65c413-e8f7-4e46-b836-3482b9729b5f.jpg"))
                        .isInstanceOf(FileNotFoundException.class);
    }


}
