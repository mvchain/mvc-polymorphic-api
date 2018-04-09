package com.mvc.polymorphic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.polymorphic.model.JsonCredentials;
import com.mvc.polymorphic.model.TransactionResponse;
import com.mvc.polymorphic.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.geth.Geth;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.web3j.tx.Contract.GAS_LIMIT;
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
    @Autowired
    private Quorum quorum;
    @Value("${trans.log.url}")
    String transLogUrl;
    @Autowired
    RestTemplate restTemplate;

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
        pass = new String(RSACoder.decryptByPrivateKey(pass, RSACoder.getPrivateKey()));
        PersonalUnlockAccount flag = admin.personalUnlockAccount(transaction.getFrom(), pass).send();
        Assert.isTrue(flag.accountUnlocked(), "unlock error");
        EthSendTransaction response = admin.ethSendTransaction(transaction).send();
        return response;
    }

    private <T, R> TransactionResponse<R> processEventResponse(
            List<T> eventResponses, TransactionReceipt transactionReceipt, java.util.function.Function<T, R> map) {
        if (!eventResponses.isEmpty()) {
            return new TransactionResponse<>(
                    transactionReceipt.getTransactionHash(),
                    map.apply(eventResponses.get(0)));
        } else {
            return new TransactionResponse<>(
                    transactionReceipt.getTransactionHash());
        }
    }

    @Override
    public Object eth_sendTransaction(Transaction transaction, String pass, String contractAddress) throws Exception {
        pass = new String(RSACoder.decryptByPrivateKey(pass, RSACoder.getPrivateKey()));
        PersonalUnlockAccount flag = admin.personalUnlockAccount(transaction.getFrom(), pass).send();
        Assert.isTrue(flag.accountUnlocked(), "unlock error");
        Function function = new Function("transfer", Arrays.<Type>asList(new Address(transaction.getTo()), new Uint256(Numeric.decodeQuantity(transaction.getValue()))), Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        PrivateTransaction privateTransaction = new PrivateTransaction(transaction.getFrom(), null,GAS_LIMIT, contractAddress, BigInteger.ZERO, data,Arrays.asList(transaction.getFrom(), transaction.getTo(), "0xc83783e5f32d1157498e6374b6ab2aec48ff4428") );
        EthSendTransaction response = quorum.ethSendTransaction(privateTransaction).send();

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

    @Override
    public Object txList(String address) {


        String url = transLogUrl + String.format( EtherscanUrl.txlist, address);
        ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, null, String.class);
        System.out.println(response.getHeaders());
        return response.getBody();
    }
}
