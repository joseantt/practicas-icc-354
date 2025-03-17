package org.example.practica3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@SpringBootApplication
public class Practica3Application {

    public static void main(String[] args) {
        SpringApplication.run(Practica3Application.class, args);
    }

}
