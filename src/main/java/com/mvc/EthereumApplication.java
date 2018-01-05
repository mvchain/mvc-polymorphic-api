package com.mvc;

import com.spring4all.swagger.EnableSwagger2Doc;
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
@EnableSwagger2Doc
public class EthereumApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(EthereumApplication.class, args);
    }

}
