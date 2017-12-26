package io.yope.ethereum.rpc.services;

import com.cegeka.tetherj.EthCall;
import com.cegeka.tetherj.EthSmartContract;
import com.cegeka.tetherj.EthSmartContractFactory;
import com.cegeka.tetherj.NoSuchContractMethod;
import com.cegeka.tetherj.crypto.CryptoUtil;
import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.ContractData;
import com.cegeka.tetherj.pojo.Transaction;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.EthTransaction;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.rpc.EthereumRpc;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;
import java.util.concurrent.*;

import static io.yope.ethereum.utils.EthereumUtil.adapt;
import static io.yope.ethereum.utils.EthereumUtil.decryptQuantity;

@AllArgsConstructor
@Slf4j
public class ContractService {

    /*
    timeout in milliseconds of receipt waiting time.
     */
    private static final long TIMEOUT = 100000;

    private EthereumRpc ethereumRpc;

    private long gasPrice;


    /**
     * Creates a contract into Ethereum. It returns a map of futures receipts.
     * @param visitor
     * @param accountGas
     * @return
     * @throws ExceededGasException
     * @throws NoSuchContractMethod
     */
    public Future<Receipt> create(final BlockchainVisitor visitor, final long accountGas)
            throws ExceededGasException, NoSuchContractMethod {
        String content = getContent(visitor);
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(content
                );
        ContractData contract = compiled.getContractData().get(visitor.getName());
        String code = contract.getCode();
        String subCode = code.substring(2, code.length());

        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(
                EthTransaction.builder().data(subCode).from(visitor.getAccount().getAddress()).build()
        ));
        checkGas(visitor.getAccount().getAddress(), accountGas, gas);
        String txHash = ethereumRpc.eth_sendTransaction(
                EthTransaction.builder().data(subCode).from(visitor.getAccount().getAddress()).gas(gas).gasPrice(gasPrice).build());
        Future<Receipt> future = null;
        return getFutureReceipt(txHash, null, Receipt.Type.CREATE, visitor.getAccount().getAddress());
    }

    /**
     * Send a transaction from an account to another.
     * @param from
     * @param to
     * @param amount
     * @return
     */
    public Future<Receipt> sendTransaction(final String from, final String to, long amount) {
        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(EthTransaction.builder().from(from).to(to).value(amount).build()));
        String txHash = null;
        try {
//            Block latest = ethereumRpc.eth_getBlockByNumber("latest", false);
//            long nonce = decryptQuantity(latest.getNonce());

            txHash = ethereumRpc.eth_sendTransaction(
                    EthTransaction.builder().from(from).to(to).gas(gas).gasPrice(gasPrice).value(amount).build());
            return getFutureReceipt(txHash, null, Receipt.Type.CREATE, to);

        } catch (RuntimeException e) {
            log.error("transaction error", e);
        }
        return null;
    }

    /**
     * Modify the contract state, through storage update, into Ethereum. It returns a future receipt.
     * @param contractAddress
     * @param visitor
     * @param accountGas
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    public Future<Receipt> modify(final String contractAddress, final BlockchainVisitor visitor, final long accountGas) throws NoSuchContractMethod, ExceededGasException {
//        addMethods(visitor);
        EthSmartContract smartContract = getSmartContract(contractAddress, visitor);
        String modMethodHash = callModMethod(smartContract, visitor.getMethod().getName(), visitor.getAccount().getAddress(), accountGas, visitor.getMethod().getArgs());
        return getFutureReceipt(modMethodHash, contractAddress, Receipt.Type.MODIFY, visitor.getAccount().getAddress());
    }

    /**
     * Run a contract method, registered into Ethereum. It returns a generic value.
     * @param contractAddress
     * @param visitor
     * @param
     * @return
     * @throws NoSuchContractMethod
     */
    public Object[] run(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod {
        EthSmartContract smartContract = getSmartContract(contractAddress, visitor);
        return callConstantMethod(smartContract, visitor.getMethod().getName(), visitor.getAccount().getAddress(), visitor.getMethod().getArgs());
    }

    private String getContent(BlockchainVisitor visitor) {
        Object[] args = visitor.getCreateArgs();
        return MessageFormat.format( adapt(visitor.getContent(), args.length), args);
//        return visitor.getContent();
    }


    private void checkGas(final String accountAddress, final long accountGas, final long gas) throws ExceededGasException {
        if (accountGas < gas) {
            throw new ExceededGasException("gas exceeded for account " + accountAddress + ". Needed: " + gas + " Available: " + accountGas + " ");
        }
    }

    private Object[] callConstantMethod(final EthSmartContract smartContract, final String method, final String from, final Object... args) throws NoSuchContractMethod {
        EthCall ethCall = smartContract.callConstantMethod(method, args);
        ethCall.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        ethCall.setFrom(from);
        String callMethod = ethereumRpc.eth_call(ethCall.getCall());
        return ethCall.decodeOutput(callMethod);
    }

    private String callModMethod(final EthSmartContract smartContract,final String method, final String accountAddress, final long accountGas, Object... args)
            throws NoSuchContractMethod, ExceededGasException {
        com.cegeka.tetherj.EthTransaction ethTransaction = smartContract.callModMethod(method, args);
        ethTransaction.setGasLimit(com.cegeka.tetherj.EthTransaction.maximumGasLimit);
        EthTransaction.Builder builder = EthTransaction.builder()
                .data(CryptoUtil.byteToHex((ethTransaction.getData())))
                .gasPrice(gasPrice)
                .from(accountAddress)
                .to(ethTransaction.getTo());
        EthTransaction tx = builder.build();
        long gas = decryptQuantity(ethereumRpc.eth_estimateGas(tx));
        checkGas(accountAddress, accountGas, gas);
        return ethereumRpc.eth_sendTransaction(builder.gas(gas).build());
    }

    private EthSmartContract getSmartContract(final String contractAddress, final BlockchainVisitor visitor) {
        CompileOutput compiled =
                ethereumRpc.eth_compileSolidity(
                        getContent(visitor)
                );
        ContractData contract = compiled.getContractData().get(visitor.getName());
        EthSmartContractFactory factory = new EthSmartContractFactory(contract);
        return factory.getContract(contractAddress);
    }

    private Future<Receipt> getFutureReceipt(final String txHash, final String contractAddress, final Receipt.Type type, final String accountAddr) {
        ExecutorService threadpool = Executors.newSingleThreadExecutor();
        ReceiptTask task = new ReceiptTask(txHash, ethereumRpc, contractAddress, type, accountAddr);
        CompletionService<Receipt> completionService = new ExecutorCompletionService(threadpool);
        return completionService.submit(task);
    }

    /**
     * Receipt asynchronous task.
     */
    private static class ReceiptTask implements Callable {

        private static final long RECEIPT_TIMEOUT = 1000;
        private String txHash;
        private EthereumRpc ethereumRpc;
        private String contractAddress;
        private Receipt.Type type;
        private String accountAddr;

        public ReceiptTask(final String txHash, final EthereumRpc ethereumRpc, final String contractAddress, final Receipt.Type type) {
            this(txHash, ethereumRpc, contractAddress, type, null);
        }

        public ReceiptTask(final String txHash, final EthereumRpc ethereumRpc, final String contractAddress, final Receipt.Type type, final String accountAddr) {
            this.txHash = txHash;
            this.ethereumRpc = ethereumRpc;
            this.contractAddress = contractAddress;
            this.type = type;
            this.accountAddr = accountAddr;
        }

        @Override
        public Receipt call() {
            Transaction transaction = ethereumRpc.eth_getTransactionByHash(txHash);
            while(ethereumRpc.eth_getTransactionReceipt(txHash) == null) {
                log.trace("waiting for {} receipt...", txHash);
                try {
                    Thread.sleep(RECEIPT_TIMEOUT);
                } catch (InterruptedException e) {
                }
            }
            Receipt receipt = ethereumRpc.eth_getTransactionReceipt(txHash);
            receipt.setType(type);
            if (contractAddress != null) {
                receipt.setContractAddress(contractAddress);
            }
            receipt.setAccountAddr(accountAddr);
            log.debug("receipt: {}", receipt);
            return receipt;
        }

    }
}
