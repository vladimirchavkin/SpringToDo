package com.emobile.springtodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@SpringBootApplication
@EnableCaching
@EnableJdbcRepositories
public class SpringToDoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringToDoApplication.class, args);
    }

}
