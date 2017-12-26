package io.yope.ethereum.rpc.services;

import io.yope.ethereum.model.Account;
import io.yope.ethereum.rpc.EthereumRpc;
import lombok.AllArgsConstructor;

import java.math.BigInteger;

@AllArgsConstructor
public class AccountService {

    private static final long WEI_TO_SZABO = 1000000000000L;

    private EthereumRpc ethereumRpc;

    /**
     * Returns an account with its balance.
     * @param address
     * @return
     */
    public Account getAccount(final String address) {
        return Account.builder().balance(getBalance(address)).address(address).build();
    }

    /**
     * Create a fresh account and unlock it.
     * @param passhphrase
     * @return
     */
    public Account createAccount(final String passhphrase) {
        String address = ethereumRpc.personal_newAccount(passhphrase);
        ethereumRpc.personal_unlockAccount(address, passhphrase);
        return Account.builder().address(address).build();
    }

    /**
     * Unlock an already existent account.
     * @param account
     * @return
     */
    public boolean unlockAccount(final Account account) {
        return ethereumRpc.personal_unlockAccount(account.getAddress(), account.getPassphrase());
    }

    /**
     * account balance in szabo. (1 ether = 1.000.000 szabo)
     * @param address
     * @return
     */
    private long getBalance(final String address) {
        String balance = ethereumRpc.eth_getBalance(address, "latest");
        BigInteger latestBalance = new BigInteger(
                "00" + balance.substring(2), 16);
        return latestBalance.divide(BigInteger.valueOf(WEI_TO_SZABO)).longValue();
//        return latestBalance.longValue();
    }



}
