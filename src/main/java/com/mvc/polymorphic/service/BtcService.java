package com.mvc.polymorphic.service;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.common.bean.BchTransaction;
import com.mvc.polymorphic.common.bean.BtcTransaction;
import com.mvc.polymorphic.configuration.TokenConfig;
import lombok.extern.java.Log;
import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.TransactionConfidenceEventListener;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.listeners.KeyChainEventListener;
import org.bitcoinj.wallet.listeners.ScriptsChangeEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsReceivedEventListener;
import org.bitcoinj.wallet.listeners.WalletCoinsSentEventListener;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "BtcService")
@Primary
@Log
public class BtcService extends BlockChainService {

    private static final String symbol = "btc";
    private final static String DEFAULT_FILE_PREFIX = "DEFAULT_FILE_PREFIX";

    private boolean initFinished = false;

    private WalletAppKit kit;

    @Override
    public BlockResult getBalance(String address) {
        if (!initFinished) {
            return tokenFail(symbol, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        return tokenSuccess(symbol, kit.wallet().getBalance().toFriendlyString());
    }

    @Override
    public BlockResult getTransactionByHash(String transactionHash) {
        if (!initFinished) {
            return tokenFail(symbol, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        org.bitcoinj.core.Transaction trans = kit.wallet().getTransaction(org.bitcoinj.core.Sha256Hash.wrap(transactionHash));
        BtcTransaction transaction = BtcTransaction.build(trans, kit.wallet());
        return tokenSuccess(symbol, transaction);
    }

    @Override
    public BlockResult sendTransaction(String pass, String from, String to, BigDecimal value) {
        if (!initFinished) {
            return tokenFail(symbol, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        return null;
    }

    @Override
    /** Useless password. */
    public BlockResult newAccount(String pass) {
        if (!initFinished) {
            return tokenFail(symbol, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        Address address = kit.wallet().freshReceiveAddress();
        kit.wallet().addWatchedAddress(address);
        return tokenSuccess(symbol, address.toString());
    }

    @Override
    protected BlockResult getConfirmation(String transactionHash) {
        if (!initFinished) {
            return tokenFail(symbol, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        BtcTransaction transaction = (BtcTransaction) getTransactionByHash(transactionHash).getResult();
        return tokenSuccess(symbol, transaction.getDepth());
    }

    @Override
    public void onTransaction(Object... objects) {

    }

    @Override
    public void run(String... strings) throws Exception {
        String allEnv = tokenConfig.getEnv().get("all");
        String env = allEnv != null ? allEnv : tokenConfig.getEnv().get(symbol);
        String path = tokenConfig.getPath().get(symbol).get(env);
        kit = new WalletAppKit(this.getNetWork(env), new File(path + env), DEFAULT_FILE_PREFIX);
        startListen();
        // init wallet
        if (kit.wallet().getImportedKeys().size() == 0) {
            String pass = tokenConfig.getPass().get(symbol).get(env);
            ECKey ecKey = new ECKey();
            kit.wallet().encrypt(pass);
            kit.wallet().importKeysAndEncrypt(Arrays.asList(ecKey), pass);
            kit.wallet().addWatchedAddress(ecKey.toAddress(kit.params()));
        }
        initFinished = true;
        log.info(String.format("BtcService initialized and block path is: %s", path));
    }

    private void startListen() {
        log.info("Start peer group");
        kit.startAsync();
        kit.awaitRunning();
        log.info("Downloading block chain");
        log.info("start listen");
        kit.wallet().addCoinsReceivedEventListener(new WalletCoinsReceivedEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction transaction, Coin coin, Coin coin1) {
                System.out.println("coins received");
                Map<String, String> map = new HashMap<>();
                onTransaction(map);
            }
        });

        kit.wallet().addCoinsSentEventListener(new WalletCoinsSentEventListener() {
            @Override
            public void onCoinsSent(Wallet wallet, Transaction tx, Coin prevBalance, Coin newBalance) {
                System.out.println("coins sent");
                Map<String, String> map = new HashMap<>();
                onTransaction(map);
            }
        });

        kit.wallet().addKeyChainEventListener(new KeyChainEventListener() {
            @Override
            public void onKeysAdded(List<ECKey> keys) {
                System.out.println("new key added");
                Map<String, String> map = new HashMap<>();
                onTransaction(map);
            }
        });

        kit.wallet().addScriptsChangeEventListener(new ScriptsChangeEventListener() {
            @Override
            public void onScriptsChanged(Wallet wallet, List<Script> scripts, boolean isAddingScripts) {
                System.out.println("new script added");
                Map<String, String> map = new HashMap<>();
                onTransaction(map);
            }
        });

        kit.wallet().addTransactionConfidenceEventListener(new TransactionConfidenceEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                System.out.println("-----> confidence changed: " + tx.getHashAsString());
                TransactionConfidence confidence = tx.getConfidence();
                System.out.println("new block depth: " + confidence.getDepthInBlocks());
                Map<String, String> map = new HashMap<>();
                onTransaction(map);
            }
        });
    }


    private NetworkParameters getNetWork(String env) {
        NetworkParameters params = null;
        if (TokenConfig.ENV_LOCAL.equalsIgnoreCase(env) || TokenConfig.ENV_TEST.equals(env)) {
            params = TestNet3Params.get();
        } else {
            params = MainNetParams.get();
        }
        return params;
    }
}
