/**
 *
 */
package com.mvc.polymorphic.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class RpcConfiguration {

    @Value("${org.ethereum.address}")
    private String ethereumAddress;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Web3j web3j() {
        return Web3j.build(new HttpService(ethereumAddress));
    }

    @Bean
    public Admin admin() {

        return  Admin.build(new HttpService(ethereumAddress));
    }

    @Bean
    public Geth geth() {
        return  Geth.build(new HttpService(ethereumAddress));
    }

    @Bean
    public Quorum quorum() {
        return Quorum.build(new HttpService(ethereumAddress));
    }
}
