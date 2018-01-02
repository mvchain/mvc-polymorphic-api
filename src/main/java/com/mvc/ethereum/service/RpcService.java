package com.mvc.ethereum.service;

import org.ethereum.jsonrpc.JsonRpc;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthTransaction;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.ExecutionException;

/**
 * 远程调用
 */
public interface RpcService {

    Object eth_getBalance(String address, String blockId) throws Exception;

    Object eth_getTransactionByHash(String transactionHash) throws Exception;

    Object eth_sendTransaction(Transaction transaction, String pass) throws Exception;

    Object personal_newAccount(String passhphrase) throws Exception;

    Object personal_listAccounts() throws IOException;

    Object personal_importRawKey(String keydata, String passphrase) throws Exception;

    Object parityExportAccount(String address, String passphrase) throws IOException;

    Object ethSendRawTransaction(String signedMessage) throws Exception;

    Object getTransactionCount(String address) throws ExecutionException, InterruptedException;

    Object eth_personalByKeyDate(String source, String passhphrase) throws Exception;

    Object eth_personalByPrivateKey(String privateKey) throws Exception;
}
