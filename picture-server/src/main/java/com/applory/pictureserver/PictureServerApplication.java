package com.applory.pictureserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PictureServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PictureServerApplication.class, args);
    }

}
