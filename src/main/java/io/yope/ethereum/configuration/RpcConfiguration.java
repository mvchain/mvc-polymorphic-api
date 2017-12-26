/**
 *
 */
package io.yope.ethereum.configuration;

import io.yope.ethereum.rpc.EthereumResource;
import io.yope.ethereum.rpc.EthereumRpc;
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


}
