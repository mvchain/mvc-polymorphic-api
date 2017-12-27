package com.mvc.ethereum.configuration;

import com.mvc.ethereum.rpc.EthereumRpc;
import com.mvc.ethereum.rpc.services.AccountService;
import com.mvc.ethereum.rpc.services.ContractService;
import com.mvc.ethereum.rpc.services.EthereumFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public EthereumFacade facade(final ContractService contractService, final AccountService accountService) {
        return new EthereumFacade(contractService, accountService, registrationTip, centralAccount);
    }

    @Bean
    public ContractService contractService(EthereumRpc ethereumRpc) {
//        boolean unlocked = ethereumRpc.personal_unlockAccount(centralAccount, password);
//        log.info("central wallet {} unlocked: {}", centralAccount, unlocked);
//        long gasPrice = decryptQuantity(ethereumRpc.eth_gasPrice());
        return new ContractService(ethereumRpc, 10000);
    }

    @Bean
    public AccountService accountService(EthereumRpc ethereumRpc) {
        return new AccountService(ethereumRpc);
    }

}
