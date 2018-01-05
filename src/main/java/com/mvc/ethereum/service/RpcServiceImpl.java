package com.mvc.ethereum.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.ethereum.model.JsonCredentials;
import com.mvc.ethereum.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
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
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.geth.Geth;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.request.PrivateTransaction;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
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
        admin.personalUnlockAccount(address, "mvc123$%^").send();
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
    public Object eventLog(String address) throws IOException {
//        EthFilter filter = new EthFilter(
//                DefaultBlockParameter.valueOf(Numeric.toBigInt("0xe8")),
//                DefaultBlockParameter.valueOf("latest"), "0x58f103adabe28d60febfb2fb732fef8c7acdbda3");
//        filter.addNullTopic();
//        EthLog ethLog = web3j.ethGetLogs(filter)
//                .send();
        
        DefaultBlockParameterName start = DefaultBlockParameterName.EARLIEST;
        DefaultBlockParameterName end = DefaultBlockParameterName.LATEST;
        EthFilter ethFilter = new EthFilter(start, end, address);

        ethFilter.addOptionalTopics("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                "0x00000000000000000000000058f103adabe28d60febfb2fb732fef8c7acdbda3",
                "0x00000000000000000000000017c6e1ecd0518a79928c80cd75114d1b9e1acc90");
//        ethFilter.addSingleTopic("0x00000000000000000000000017c6e1ecd0518a79928c80cd75114d1b9e1acc90");
//        org.web3j.protocol.core.methods.response.EthFilter ethNewFilter = web3j.ethNewFilter(ethFilter).send();

//        BigInteger filterId = ethNewFilter.getFilterId();
//        EthLog ethFilterLogs = web3j.ethGetFilterLogs(filterId).send();
//        new org.web3j.protocol.core.filters.Filter().run();
//        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();
        return web3j.ethLogObservable(ethFilter).asObservable();
    }
}
