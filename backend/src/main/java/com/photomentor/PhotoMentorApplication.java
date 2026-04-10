package com.photomentor;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.photomentor.mapper")
public class PhotoMentorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoMentorApplication.class, args);
    }
}
