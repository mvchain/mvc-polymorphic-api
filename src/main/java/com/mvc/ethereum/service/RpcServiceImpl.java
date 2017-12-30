package com.mvc.ethereum.service;

import com.mvc.ethereum.utils.Denomination;
import com.mvc.ethereum.utils.RSACoder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.ethereum.jsonrpc.JsonRpc;
import org.ethereum.jsonrpc.TypeConverter;
import org.ethereum.net.eth.handler.Eth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.NewAccountIdentifier;
import org.web3j.protocol.admin.methods.response.PersonalListAccounts;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.ShhFilter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.Web3Sha3;
import org.web3j.protocol.geth.Geth;
import org.web3j.protocol.parity.Parity;
import org.web3j.protocol.parity.methods.response.ParityExportAccount;
import org.web3j.protocol.rx.Web3jRx;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.concurrent.CompletableFuture;

import static org.web3j.utils.Convert.*;

@Component
public class RpcServiceImpl implements RpcService {
    @Autowired
    private Admin admin;
    @Autowired
    private Geth geth;
    @Autowired
    private Web3j web3j;
    @Autowired
    private Parity parity;

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
        pass = new String(RSACoder.decryptByPrivateKey(pass, RSACoder.getPrivateKey()));
        PersonalUnlockAccount flag = admin.personalUnlockAccount(transaction.getFrom(), pass).send();
        Assert.isTrue(flag.accountUnlocked(), "unlock error");
        EthSendTransaction response = admin.ethSendTransaction(transaction).send();
//
//        web3j.ethSendTransaction(transaction);
//        boolean flag = ethereumService.personal_unlockAccount(from, pass);

//        JsonRpc.CallArguments callArguments = new JsonRpc.CallArguments();
//        callArguments.data = "0x";
//        callArguments.from = transaction.getFrom();
//        callArguments.gas = null == transaction.getGas() ? transaction.getGas() : String.valueOf(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(transaction.getGas())));
//        callArguments.gasPrice = null == transaction.getGasPrice() ? transaction.getGasPrice() : String.valueOf(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(transaction.getGasPrice())));
//        callArguments.to = transaction.getTo();
//        callArguments.nonce = transaction.getNonce();
//        callArguments.value = TypeConverter.toJsonHex(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(transaction.getValue())).toString(16));
//        String result = ethereumService.eth_sendTransaction(callArguments);
        return response;
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
    public Object personal_importRawKey(String keydata, String passphrase) throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, CipherException {
        // 以下代码用于文件类型的导入, 暂时不处理
//        Credentials str = WalletUtils.loadCredentials(passphrase, "C:\\Users\\ethands\\AppData\\Roaming\\Ethereum\\rinkeby\\keystore\\keystore\\UTC--2017-12-28T07-31-33.459999300Z--e242a8871d2538714edb9e87e6af3694d753e8aa");
//        str.getEcKeyPair().getPrivateKey().toString(16);

        parity.parityExportAccount("0xe242a8871d2538714edb9e87e6af3694d753e8aa", "mvc123$%^").send();
        return geth.personalImportRawKey(keydata, passphrase).send();
    }

    @Override
    public Object parityExportAccount(String address, String passphrase) throws IOException {
        ParityExportAccount response = parity.parityExportAccount(address, passphrase).send();
        return response;
    }

}
