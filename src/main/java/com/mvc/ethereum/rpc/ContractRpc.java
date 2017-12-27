package com.mvc.ethereum.rpc;

import com.cegeka.tetherj.pojo.CompileOutput;
import com.cegeka.tetherj.pojo.Transaction;
import com.cegeka.tetherj.pojo.TransactionCall;
import com.mvc.ethereum.model.Block;
import com.mvc.ethereum.model.EthTransaction;
import com.mvc.ethereum.model.Filter;
import com.mvc.ethereum.model.Receipt;

public interface ContractRpc {

  CompileOutput eth_compileSolidity(String sourceCode);

  String eth_sendTransaction(EthTransaction tx);

  Receipt eth_getTransactionReceipt(String txHash);

  Transaction eth_getTransactionByHash(String hash);

  String eth_call(TransactionCall call);

  String eth_newFilter(Filter filter);

  String eth_estimateGas(EthTransaction tx);

  String eth_gasPrice();

  Block eth_getBlockByNumber(String blockParam, boolean transactions);
}
