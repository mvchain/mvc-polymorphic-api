package com.mvc.polymorphic.service;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.configuration.TokenConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EthService extends BlockChainService {

    public static final String symbol = "ETH";

    private String gethUrl;

    @Autowired
    private TokenConfig tokenConfig;

    @Override
    protected BlockResult getBalance(String address) {
        return null;
    }

    @Override
    protected BlockResult getTransactionByHash(String transactionHash) {
        return null;
    }

    @Override
    protected BlockResult sendTransaction(String pass, String from, String to, BigDecimal value) {
        return null;
    }

    @Override
    protected BlockResult newAccount(String pass) {
        return null;
    }

    @Override
    protected void onTransaction(Object... objects) {

    }

    @Override
    public void run(String... args) throws Exception {
        gethUrl = tokenConfig.getUrl().get(symbol).get(tokenConfig.getEnv().get(symbol));
        System.out.println("EthService initialized and geth Url is :" + gethUrl);
    }
}
