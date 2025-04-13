package com.fiap.hackaton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FileProcessConsumerApp {

    public static void main(String[] args) {
        SpringApplication.run(FileProcessConsumerApp.class, args);
    }

}
