package com.mvc.ethereum.rpc;

public interface WalletRpc {
  String eth_getBalance(String address, String defaultBlock);
}
