package com.mvc.polymorphic.common;

import com.mvc.polymorphic.configuration.TokenConfig;
import com.mvc.tools.context.BaseContextHandler;
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
    protected TokenConfig tokenConfig;
    @Autowired
    protected BlockConfig blockConfig;

    private BlockChainService getService(String serviceName) {
        BlockChainService service = SpringContextUtil.getBean(serviceName);
        if (null == service) {
            throw new BlockException(String.format("%s is not found", serviceName));
        }
        return service;
    }

    public BlockResult getBalance(String serviceName, String address) throws Exception {
        return getService(serviceName).getBalance(address);
    }

    protected abstract BlockResult getBalance(String address) throws Exception;

    public BlockResult getTransactionByHash(String serviceName, String transactionHash) throws Exception {
        return getService(serviceName).getTransactionByHash(transactionHash);
    }

    protected abstract BlockResult getTransactionByHash(String transactionHash) throws Exception;

    public BlockResult sendTransaction(String serviceName, String pass, String from, String to, BigDecimal value) throws Exception {
        return getService(serviceName).sendTransaction(pass, from, to, value);
    }

    protected abstract BlockResult sendTransaction(String pass, String from, String to, BigDecimal value) throws Exception;

    public BlockResult newAccount(String serviceName, String pass) {
        return getService(serviceName).newAccount(pass);
    }

    protected abstract BlockResult newAccount(String pass);

    public BlockResult getConfirmation(String serviceName, String transactionHash) throws Exception {
        return getService(serviceName).getConfirmation(transactionHash);
    }

    protected abstract BlockResult getConfirmation(String transactionHash) throws Exception;

    protected abstract void onTransaction(Object... objects);

    protected BlockResult tokenSuccess(String tokenName, Object result) {
        return new BlockResult(tokenName, true, null, result);
    }

    protected BlockResult tokenFail(String tokenName, String msg) {
        return new BlockResult(tokenName, false, msg, null);
    }

    protected String getType() {
        return (String) BaseContextHandler.get("type");
    }
}
