package com.stanum.skrudzh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SkrudzhApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkrudzhApiApplication.class, args);
    }
}
