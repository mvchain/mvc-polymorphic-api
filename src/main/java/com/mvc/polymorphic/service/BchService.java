package com.mvc.polymorphic.service;

import com.mvc.bitcoincashj.core.*;
import com.mvc.bitcoincashj.core.listeners.TransactionConfidenceEventListener;
import com.mvc.bitcoincashj.kits.WalletAppKit;
import com.mvc.bitcoincashj.params.MainNetParams;
import com.mvc.bitcoincashj.params.TestNet3Params;
import com.mvc.bitcoincashj.script.Script;
import com.mvc.bitcoincashj.wallet.Wallet;
import com.mvc.bitcoincashj.wallet.listeners.KeyChainEventListener;
import com.mvc.bitcoincashj.wallet.listeners.ScriptsChangeEventListener;
import com.mvc.bitcoincashj.wallet.listeners.WalletCoinsReceivedEventListener;
import com.mvc.bitcoincashj.wallet.listeners.WalletCoinsSentEventListener;
import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.common.bean.BchTransaction;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * bcc service
 *
 * @author qiyichen
 * @create 2018/4/12 13:43
 */
@Service("BchService")
@Log
public class BchService extends BlockChainService {

    private WalletAppKit kit;
    private final static String DEFAULT_FILE_PREFIX = "DEFAULT_FILE_PREFIX";
    private final static String TEST_KEY = "local";
    private final static String TOKEN_NAME = "bch";
    private static Boolean initFinished = false;

    @Override
    protected BlockResult getBalance(String address) {
        Object result = kit.wallet().getBalance().toFriendlyString();
        return tokenSuccess(TOKEN_NAME, result);
    }

    @Override
    protected BlockResult getTransactionByHash(String transactionHash) {
        Transaction trans = kit.wallet().getTransaction(Sha256Hash.wrap(transactionHash));
        BchTransaction transaction = BchTransaction.build(trans, kit.wallet());
        return tokenSuccess(TOKEN_NAME, transaction);
    }

    @Override
    protected BlockResult sendTransaction(String pass, String from, String toAddress, BigDecimal data) {
        String blockEnv = tokenConfig.getEnv().get(TOKEN_NAME);
        if (!initFinished) {
            return tokenFail(TOKEN_NAME, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        log.info("send money to: " + toAddress);
        Coin value = Coin.parseCoin(String.valueOf(data));
        // if the wallet have more than 1 ecKey, we need to choose one for pay
        Address to = Address.fromBase58(kit.params(), toAddress);
        Wallet.SendResult result = null;
        try {
            if (TEST_KEY.equalsIgnoreCase(blockEnv)) {
                kit.peerGroup().setMaxConnections(4);
            }
            result = kit.wallet().sendCoins(kit.peerGroup(), to, value, true);
        } catch (Exception e) {
            return tokenFail(TOKEN_NAME, e.getMessage());
        } finally {
            if (!kit.wallet().isEncrypted()) {
                kit.wallet().encrypt(pass);
            }
        }
        log.info("coins sent. transaction hash: " + result.tx.getHashAsString());
        return tokenSuccess(TOKEN_NAME, result.tx.getHashAsString());
    }

    @Override
    protected BlockResult newAccount(String pass) {
        if (!initFinished) {
            return tokenFail(TOKEN_NAME, String.format("wallet is async, please wait, now height is %s", kit.wallet().getLastBlockSeenHeight()));
        }
        Address address = kit.wallet().freshReceiveAddress();
        kit.wallet().addWatchedAddress(address);
        return tokenSuccess(TOKEN_NAME, address.toString());
    }

    @Override
    protected BlockResult getConfirmation(String transactionHash) {
        BchTransaction transaction = (BchTransaction) getTransactionByHash(transactionHash).getResult();
        return tokenSuccess(TOKEN_NAME, transaction.getDepth());
    }

    @Override
    protected void onTransaction(Object... objects) {
        System.out.println("db save");
    }

    @Override
    public void run(String... args) throws Exception {
        String blockEnv = tokenConfig.getEnv().get(TOKEN_NAME);
        String blockPath = tokenConfig.getPath().get(TOKEN_NAME).get(blockEnv);
        WalletAppKit kit = new WalletAppKit(getNetWork(blockEnv), new File(blockPath + blockEnv), DEFAULT_FILE_PREFIX);
        this.kit = kit;
        System.out.println("BchService initialized and nothing happened.");
        startListen();
        // init wallet
        if (kit.wallet().getImportedKeys().size() == 0) {
            String pass = tokenConfig.getPass().get(TOKEN_NAME).get(blockEnv);
            ECKey ecKey = new ECKey();
            kit.wallet().encrypt(pass);
            kit.wallet().importKeysAndEncrypt(Arrays.asList(ecKey), pass);
            kit.wallet().addWatchedAddress(ecKey.toAddress(kit.params()));
        }
        initFinished = true;
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

    /**
     * get net work by env
     *
     * @return
     */
    public NetworkParameters getNetWork(String blockEnv) {
        NetworkParameters params = null;
        if (TEST_KEY.equalsIgnoreCase(blockEnv)) {
            params = TestNet3Params.get();
        } else {
            params = MainNetParams.get();
        }
        return params;
    }
}
