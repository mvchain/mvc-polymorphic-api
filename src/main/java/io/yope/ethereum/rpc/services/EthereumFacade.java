package io.yope.ethereum.rpc.services;

import com.cegeka.tetherj.NoSuchContractMethod;
import io.yope.ethereum.exceptions.ExceededGasException;
import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Receipt;
import io.yope.ethereum.visitor.BlockchainVisitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@AllArgsConstructor
public class EthereumFacade implements BlockchainFacade {

    private static final long TIMEOUT = 100000;

    private ContractService contractService;

    private AccountService accountService;

    private long registrationTip;

    private String centralAccount;

    @Override
    public Future<Receipt> createContract(final BlockchainVisitor visitor) throws ExceededGasException, NoSuchContractMethod {
        Account account = verifyAccount(visitor);
        return contractService.create(visitor, getAccount(account.getAddress()).getBalance());
    }

    @Override
    public Future<Receipt> modifyContract(final String contractAddress, final BlockchainVisitor visitor) throws NoSuchContractMethod, ExceededGasException {
        return contractService.modify(contractAddress, visitor, getAccount(visitor.getAccount().getAddress()).getBalance());
    }

    @Override
    public Object[] runContract(final String contractAddress, final BlockchainVisitor visitor)
            throws NoSuchContractMethod {
        return contractService.run(contractAddress, visitor);
    }

    private Account verifyAccount(BlockchainVisitor visitor) {
        Account account;
        if (StringUtils.isBlank(visitor.getAccount().getAddress())) {
            account = createAccount(visitor.getAccount().getPassphrase());
            sendTransaction(account);
            visitor.setAccount(account);
        } else {
            account = visitor.getAccount();
        }
        return account;
    }

    private Receipt sendTransaction(Account account) {
        if (registrationTip > 0) {
            try {
                Future<Receipt> futureTransaction = this.sendTransaction(centralAccount, account.getAddress(), registrationTip);
                return futureTransaction.get(TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error("interrupted", e);
            } catch (ExecutionException e) {
                log.error("execution", e);
            } catch (TimeoutException e) {
                log.error("timeout", e);
            } catch (RuntimeException e) {
                log.error("generic", e);
            }
        }
        return null;
    }

    @Override
    public Account getAccount(final String address) {
        return accountService.getAccount(address);
    }

    @Override
    public Account createAccount(final String passphrase) {
        return accountService.createAccount(passphrase);
    }

    @Override
    public boolean unlockAccount(final Account account) {
        return accountService.unlockAccount(account);
    }

    @Override
    public Future<Receipt> sendTransaction(String from, String to, long amount) {
        return contractService.sendTransaction(from, to, amount);
    }

}
