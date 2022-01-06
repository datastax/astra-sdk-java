package com.datastax.yasa;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class YasaApp {

    public static void main(String[] args) {
        SpringApplication.run(YasaApp.class, args);
    }
    
    @PostConstruct
    public void sample() {
        System.out.println("OK");
    }
    
}