package com.mvc.polymorphic.service;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service(value = "BtcService")
@Primary
public class BtcService extends BlockChainService {

    @Override
    public BlockResult getBalance(String address) {
        return null;
    }

    @Override
    public BlockResult getTransactionByHash(String transactionHash) {
        return null;
    }

    @Override
    public BlockResult sendTransaction(String pass, String from, String to, BigDecimal value) {
        return null;
    }

    @Override
    public BlockResult newAccount(String pass) {
        return null;
    }

    @Override
    public void onTransaction() {

    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("BtcService initialized and nothing happened.");
    }
}
