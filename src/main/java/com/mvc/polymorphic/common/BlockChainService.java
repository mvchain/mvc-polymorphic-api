package com.mvc.polymorphic.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author ethands
 */
@Service
public abstract class BlockChainService implements CommandLineRunner {

    @Autowired
    protected BlockConfig blockConfig;

    private BlockChainService getService(String serviceName) {
        BlockChainService service = SpringContextUtil.getBean(serviceName);
        if (null == service) {
            throw new BlockException(String.format("%s is not found", serviceName));
        }
        return service;
    }

    public BlockResult getBalance(String serviceName, String address) {
        return getService(serviceName).getBalance(address);
    }

    abstract BlockResult getBalance(String address);


    public BlockResult getTransactionByHash(String serviceName, String transactionHash) {
        return getService(serviceName).getTransactionByHash(transactionHash);
    }

    abstract BlockResult getTransactionByHash(String transactionHash);

    public BlockResult sendTransaction(String serviceName, String pass, String from, String to, BigDecimal value) {
        return getService(serviceName).sendTransaction(pass, from, to, value);
    }

    abstract BlockResult sendTransaction(String pass, String from, String to, BigDecimal value);

    public BlockResult newAccount(String serviceName, String pass) {
        return getService(serviceName).newAccount(pass);
    }

    abstract BlockResult newAccount(String pass);

    abstract void onTransaction();

}
