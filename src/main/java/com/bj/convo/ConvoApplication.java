package com.bj.convo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ConvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvoApplication.class, args);
    }

}
