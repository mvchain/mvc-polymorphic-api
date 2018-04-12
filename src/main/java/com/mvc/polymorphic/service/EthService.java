package com.mvc.polymorphic.service;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.configuration.TokenConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.geth.Geth;
import org.web3j.quorum.Quorum;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;

import static org.web3j.utils.Convert.fromWei;

@Service(value = "EthService")
public class EthService extends BlockChainService {

    public static final String symbol = "ETH";

    private String gethUrl;

    @Autowired
    private TokenConfig tokenConfig;

    @Autowired
    private Admin admin;

    @Autowired
    private Geth geth;

    @Autowired
    private Web3j web3j;

    @Autowired
    private Quorum quorum;

    @Override
    protected BlockResult getBalance(String address)throws Exception {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        BlockResult blockResult = new BlockResult(symbol, true, null,
                fromWei(String.valueOf(ethGetBalance.getBalance()), Convert.Unit.ETHER));
        return blockResult;
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
