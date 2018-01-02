package com.mvc.ethereum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.ethereum.model.JsonCredentials;
import com.mvc.ethereum.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.geth.Geth;
import sun.reflect.misc.FieldUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

import static org.web3j.utils.Convert.Unit;
import static org.web3j.utils.Convert.fromWei;

@Component
public class RpcServiceImpl implements RpcService {
    @Autowired
    private Admin admin;
    @Autowired
    private Geth geth;
    @Autowired
    private Web3j web3j;

    @Override
    public Object eth_personalByKeyDate (String source, String passhphrase) throws Exception {
        passhphrase = new String(RSACoder.decryptByPrivateKey(passhphrase, RSACoder.getPrivateKey()));
        ObjectMapper objectMapper = new ObjectMapper();
        WalletFile file = objectMapper.readValue(source, WalletFile.class);
        ECKeyPair ecKeyPair = Wallet.decrypt(passhphrase, file);
        Credentials credentials = Credentials.create(ecKeyPair);
        return new JsonCredentials(credentials);
    }

    @Override
    public Object eth_personalByPrivateKey (String privateKey) throws Exception {
        privateKey = new String(RSACoder.decryptByPrivateKey(privateKey, RSACoder.getPrivateKey()));
        return new JsonCredentials(Credentials.create(privateKey));
    }

    @Override
    public Object eth_getBalance(String address, String blockId) throws Exception {
//        admin.ethGetStorageAt(address, BigInteger.ZERO, DefaultBlockParameterName.LATEST).send()
        EthGetBalance response = web3j.ethGetBalance(address, DefaultBlockParameter.valueOf(blockId)).send();
        BigDecimal result = fromWei(String.valueOf(response.getBalance()), Unit.ETHER);
        return result;
    }

    @Override
    public Object eth_getTransactionByHash(String transactionHash) throws Exception {
        EthTransaction response = web3j.ethGetTransactionByHash(transactionHash).send();

        return response;
    }

    @Override
    public Object eth_sendTransaction(Transaction transaction, String pass) throws Exception {
//        RSACoder.encryptBASE64(RSACoder.encryptByPublicKey("mvc123$%^",RSACoder.getPublicKey()))
        pass = new String(RSACoder.decryptByPrivateKey(pass, RSACoder.getPrivateKey()));
        PersonalUnlockAccount flag = admin.personalUnlockAccount(transaction.getFrom(), pass).send();
        Assert.isTrue(flag.accountUnlocked(), "unlock error");
        EthSendTransaction response = admin.ethSendTransaction(transaction).send();
        return response;
    }

    @Override
    public Object ethSendRawTransaction(String signedMessage) throws Exception {
        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedMessage).send();
        return ethSendTransaction;
    }

    @Override
    public Object personal_newAccount(String passhphrase) throws Exception {
        passhphrase = new String(RSACoder.decryptByPrivateKey(passhphrase, RSACoder.getPrivateKey()));
        NewAccountIdentifier response = admin.personalNewAccount(passhphrase).send();
        geth.personalUnlockAccount(response.getAccountId(), passhphrase).send();
        return response;
    }

    @Override
    public Object personal_listAccounts() throws IOException {
        PersonalListAccounts response = geth.personalListAccounts().send();
        return response;
    }

    @Override
    public Object personal_importRawKey(String keydata, String passphrase) throws Exception {
        passphrase = new String(RSACoder.decryptByPrivateKey(passphrase, RSACoder.getPrivateKey()));
        keydata = new String(RSACoder.decryptByPrivateKey(keydata, RSACoder.getPrivateKey()));
        return geth.personalImportRawKey(keydata, passphrase).send();
    }

    @Override
    public Object parityExportAccount(String address, String passphrase) throws IOException {
        return null;
    }

    @Override
    public Object getTransactionCount(String address) throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        return nonce;
    }


}
