package com.mvc.ethereum.service;

import org.ethereum.jsonrpc.JsonRpc;

/**
 * 远程调用
 */
public interface RpcService {

    Object eth_getBalance(String address, String blockId) throws Exception;

    Object eth_getTransactionByHash(String transactionHash) throws Exception;

    Object eth_sendTransaction(String pass, String from, String to, String gas, String gasPrice, String value, String data, String nonce) throws Exception;

    Object personal_listAccounts() throws Exception;

}
