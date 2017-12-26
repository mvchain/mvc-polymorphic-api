package io.yope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableAutoConfiguration
@EnableScheduling
@ComponentScan({
        "io.yope.ethereum.configuration",
        "io.yope.ethereum.rest.resources"
})
public class EthereumApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(EthereumApplication.class, args);
    }
}
