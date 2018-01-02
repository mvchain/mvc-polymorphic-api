package com.mvc.ethereum.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "com.mvc.ethereum.rpc.services"
})
@EnableConfigurationProperties
public class ServiceConfiguration {

    @Value("${com.mvc.registrationTip}")
    private long registrationTip;

    @Value("${com.mvc.centralAddress}")
    private String centralAccount;

    @Value("${com.mvc.password}")
    private String password;

}
