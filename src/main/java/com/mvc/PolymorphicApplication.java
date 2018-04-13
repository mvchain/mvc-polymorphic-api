package com.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableAutoConfiguration
@EnableScheduling
@EnableSwagger2
@EnableAsync
@SpringBootApplication
public class PolymorphicApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(PolymorphicApplication.class, args);
    }

}
