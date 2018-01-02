package com.mvc.ethereum.service;

import com.mvc.ethereum.utils.RSACoder;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
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
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

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

//    static final Credentials ALICE = Credentials.create(
//            "1bbd568c95b4bc2fb75056921b781adc66dad3471d25d90e002849c46b8ef400",  // 32 byte hex value
//            "0x58f103AdABe28D60febfB2fB732FEf8C7aCDbDa3"  // 64 byte hex value
//    );


    @Override
    public Object eth_getBalance(String address, String blockId) throws Exception {
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
        // 以下代码用于文件类型的导入, 暂时不处理
//        Credentials str = WalletUtils.loadCredentials(passphrase, "C:\\Users\\ethands\\AppData\\Roaming\\Ethereum\\rinkeby\\keystore\\keystore\\UTC--2017-12-27T14-10-16.272460400Z--58f103adabe28d60febfb2fb732fef8c7acdbda3");
//        str.getEcKeyPair().getPrivateKey().toString(16);
        passphrase = new String(RSACoder.decryptByPrivateKey(passphrase, RSACoder.getPrivateKey()));
        keydata = new String(RSACoder.decryptByPrivateKey(keydata, RSACoder.getPrivateKey()));
        return geth.personalImportRawKey(keydata, passphrase).send();
    }

    @Override
    public Object parityExportAccount(String address, String passphrase) throws IOException {
        return null;
    }

}
