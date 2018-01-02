package com.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan({
        "com.mvc.ethereum",
})
public class EthereumApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(EthereumApplication.class, args);
    }
}
