package io.yope.ethereum.rpc.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;

import java.util.concurrent.Future;

/**
 * Facade for blockchain management.
 */
public interface BlockchainFacade {
    /**
     * Create an account, if empty. Give ether fuel for gas. Write contracts into the blockchain.
     * @param visitor
     * @return
     * @throws ExceededGasException
     */
    Future<Receipt> createContract(BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod;

    /**
     * Modify a contract stored into the blockchain.
     * @param visitor
     * @return
     * @throws NoSuchContractMethod
     * @throws ExceededGasException
     */
    Future<Receipt> modifyContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException;

    /**
     * Run a contract stored into the blockchain.
     * @param visitor
     * @param <T>
     * @return
     * @throws NoSuchContractMethod
     */
    Object[] runContract(String contractAddress, BlockchainVisitor visitor) throws NoSuchContractMethod;

    /**
     * Get account from Ethereum with its balance.
     * @param address
     * @return
     */
    Account getAccount(String address);

    /**
     * Create an account in Ethereum and unlock it.
     * @param passphrase
     * @return
     */
    Account createAccount(String passphrase);

    /**
     * Unlock an already existent account.
     * This method is already called during account creation. It is necessary just in case the Ethereum node is restarted.
     * @param account
     * @return
     */
    boolean unlockAccount(Account account);

    /**
     * Send eth from an account to another.
     * @param from
     * @param to
     * @param amount
     * @return
     */
    Future<Receipt> sendTransaction(String from, String to, long amount);

}
