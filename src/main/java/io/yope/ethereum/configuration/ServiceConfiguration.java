package io.yope.ethereum.configuration;

import io.yope.ethereum.model.Block;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.rpc.services.AccountService;
import io.yope.ethereum.rpc.services.ContractService;
import io.yope.ethereum.rpc.services.EthereumFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static io.yope.ethereum.utils.EthereumUtil.decryptQuantity;

@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "io.yope.ethereum.rpc.services"
})
@EnableConfigurationProperties
public class ServiceConfiguration {

    @Value("${io.yope.registrationTip}")
    private long registrationTip;

    @Value("${io.yope.centralAddress}")
    private String centralAccount;

    @Value("${io.yope.password}")
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
