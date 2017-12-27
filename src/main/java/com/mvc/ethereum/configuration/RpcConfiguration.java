/**
 *
 */
package com.mvc.ethereum.configuration;

import com.mvc.ethereum.rpc.EthereumResource;
import com.mvc.ethereum.rpc.EthereumRpc;
import com.mvc.ethereum.service.EthereumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

@Configuration
@EnableConfigurationProperties
@Slf4j
public class RpcConfiguration {

    @Value("${org.ethereum.address}")
    private String ethereumAddress;

    @Bean
    public EthereumRpc ethereumRpc() throws MalformedURLException {
        return new EthereumResource(ethereumAddress).getGethRpc();
    }

    @Bean
    public EthereumService jsonRpc() throws MalformedURLException {
        return new EthereumResource(ethereumAddress).getRpc();
    }
}
