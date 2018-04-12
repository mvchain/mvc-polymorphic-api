package com.mvc.polymorphic.service;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockException;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.configuration.TokenConfig;
import com.mvc.polymorphic.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.geth.Geth;
import org.web3j.quorum.Quorum;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.web3j.utils.Convert.fromWei;

@Service(value = "EthService")
public class EthService extends BlockChainService {

    public static final String symbol = "eth";

    private String gethUrl;

    private BigInteger gasLimit;

    private BigInteger gasPrice;

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
    protected BlockResult getTransactionByHash(String transactionHash) throws Exception {
        EthTransaction ethTransaction = web3j.ethGetTransactionByHash(transactionHash).send();
        BlockResult blockResult = new BlockResult(symbol, true, null, ethTransaction);
        BigInteger receiptBlockNumber =
                web3j.ethGetTransactionReceipt(transactionHash).send().getTransactionReceipt().get().getBlockNumber();

        BigInteger latestBlockNumber = web3j.ethBlockNumber().send().getBlockNumber();
        BigInteger blocksOnTop = latestBlockNumber.subtract(receiptBlockNumber).add(BigInteger.ONE);
        return blockResult;
    }

    @Override
    protected BlockResult sendTransaction(String pass, String from, String to, BigDecimal value) throws Exception {
        PersonalUnlockAccount flag = admin.personalUnlockAccount(from, pass).send();
        if (flag.getError() != null) throw new BlockException("Eth send - " + flag.getError().getMessage());
        Assert.isTrue(flag.accountUnlocked(), String.format("Account unlock error: %s", from));
        org.web3j.protocol.core.methods.request.Transaction transaction = new org.web3j.protocol.core.methods.request.Transaction(
                from,
                null,
                gasLimit,
                gasPrice,
                to,
                Convert.toWei(value, Convert.Unit.ETHER).toBigInteger(),
                null
        );
        EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).send();
        BlockResult blockResult = new BlockResult(symbol, true, null, ethSendTransaction);
        return blockResult;
    }

    @Override
    protected BlockResult newAccount(String pass) {
        return null;
    }

    @Override
    protected BlockResult getConfirmation(String transactionHash) {
        return null;
    }

    @Override
    protected void onTransaction(Object... objects) {

    }

    @Override
    public void run(String... args) throws Exception {
        String allEnv = tokenConfig.getEnv().get("all");
        String env = allEnv != null ? allEnv : tokenConfig.getEnv().get(symbol);
        gethUrl = tokenConfig.getUrl().get(symbol).get(env);
        gasLimit = tokenConfig.getGas().get(symbol).get(env).get("limit");
        gasPrice = tokenConfig.getGas().get(symbol).get(env).get("price");
        System.out.println("EthService initialized and geth Url is :" + gethUrl);
    }
}
