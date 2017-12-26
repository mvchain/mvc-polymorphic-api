package io.yope.ethereum.rpc.services;

import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.visitor.BlockchainVisitor;
import io.yope.ethereum.model.Method;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumResource;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.VisitorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.yope.ethereum.utils.EthereumUtil.removeLineBreaksFromFile;
import static org.junit.Assert.assertEquals;

@Slf4j
public class ContractServiceTest {

    private static final long ACCOUNT_GAS = 100000;
    private static final String ethereumAddress = "http://ethereum.yope.io";
    private static final String accountAddress = "0x03733b713032e9040d04acd4720bedaa717378df";
    private static String contractAddress = "";

    private ContractService contractService;

    private AccountService accountService;

    BlockchainVisitor visitor;

    private Method create = VisitorFactory.buildMethod(Optional.empty(), Optional.of(new Object[]{5}));
    private Method write = VisitorFactory.buildMethod(Optional.of("set"), Optional.of(new Object[]{10}));
    private Method read = VisitorFactory.buildMethod(Optional.of("get"), Optional.empty());

    @Before
    public void init() throws MalformedURLException {
        EthereumRpc ethereumRpc = new EthereumResource(ethereumAddress).getGethRpc();
        contractService = new ContractService(ethereumRpc, 20000000000L);
        accountService = new AccountService(ethereumRpc);

        visitor = VisitorFactory.build(
                contractAddress,
                accountAddress,
                null,
                "sample",
                removeLineBreaksFromFile("sample.sol", ContractServiceTest.class),
                null,
                create);
    }

    @Test
    @Ignore
    public void testSendTransaction() throws InterruptedException, ExecutionException, TimeoutException {
        Account test = accountService.createAccount("test");
        long toBalance1 = getBalance(test.getAddress());
        long fromBalance1 = getBalance(accountAddress);
        Future<Receipt> receiptFuture = contractService.sendTransaction(accountAddress, test.getAddress(), 3549);
        Receipt receipt = receiptFuture.get(10000, TimeUnit.MILLISECONDS);
        receipt.getTransactionHash();
        long fromBalance = getBalance(accountAddress);
        long toBalance = getBalance(test.getAddress());
        //0x8800cbf833fa6e2c43794324dca59a112c9d26e2c9fff52aaf5bd6ddf7741f51
    }

    private long getBalance(String addr) {
        return accountService.getAccount(addr).getBalance();
    }

    @Test
    @Ignore
    public void testGetBalance() {
        long balance = getBalance("0xca0c1a7e9bf7981dab434214c3e9bcc4518a6e2c");
        long balance1 = getBalance(accountAddress);
    }

    @Test
    @Ignore
    public <T> void testCreate() throws Exception, ExceededGasException {
        Future<Receipt> createReceipt = contractService.create(visitor, ACCOUNT_GAS);
        visitor.setMethod(read);
        int res = read(createReceipt);
        assertEquals(5, res);
        visitor.setMethod(write);
        Future<Receipt> writeReceipt = write(createReceipt);
        visitor.setMethod(read);
        res = read(writeReceipt);
        assertEquals(10, res);
    }

    private int read(Future<Receipt> receipt) throws Exception {
        String contractAddress = waitFor(receipt);
        Object[] result = contractService.<BigInteger>run(contractAddress, visitor);
        log.info("result: {}", result);
        return  ((BigInteger)result[0]).intValue();
    }

    private Future<Receipt> write(Future<Receipt> receipt) throws Exception, ExceededGasException {
        String contractAddress = waitFor(receipt);
         return contractService.modify(contractAddress, visitor, ACCOUNT_GAS);
    }


    private String waitFor(Future<Receipt> receipt) throws InterruptedException, ExecutionException {
        while(!receipt.isDone()) {
            Thread.sleep(1000);
        }
        return receipt.get().getContractAddress();
    }


}