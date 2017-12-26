package io.yope.ethereum.rpc;

public interface PersonalRpc {
    String personal_newAccount(String passphrase);
    boolean personal_unlockAccount(String address, String passphrase);
}
