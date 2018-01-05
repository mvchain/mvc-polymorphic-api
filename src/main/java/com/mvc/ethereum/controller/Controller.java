package com.mvc.ethereum.controller;

import com.mvc.ethereum.model.dto.BalanceDTO;
import com.mvc.ethereum.model.dto.SendTransactionDTO;
import com.mvc.ethereum.model.dto.TransactionCountDTO;
import com.mvc.ethereum.service.ContractService;
import com.mvc.ethereum.service.RpcService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.web3j.protocol.core.methods.request.Transaction;

/**
 * Controller for our ERC-20 contract API.
 */
@Api("ERC-20 token standard API")
@RestController
public class Controller {
    @Autowired
    private ContractService ContractService;

    @Autowired
    private RpcService rpcService;

    @ApiOperation("Get token balance for address")
    @RequestMapping(
            value = "/{contractAddress}/eth_getBalance", method = RequestMethod.POST)
    public Object balanceOf(
            @PathVariable String contractAddress,
            @RequestBody final BalanceDTO balanceDTO) {
        return ContractService.balanceOf(contractAddress, balanceDTO.getAddress());
    }

    @RequestMapping(value = "/{contractAddress}/eth_sendTransaction", method = RequestMethod.POST)
    public Object approveAndCall(@PathVariable String contractAddress, @RequestBody SendTransactionDTO sendTransactionDTO) throws Exception {
        Transaction transaction = new Transaction(sendTransactionDTO.getFrom(), sendTransactionDTO.getNonce(), sendTransactionDTO.getGasPrice(), sendTransactionDTO.getGas(), sendTransactionDTO.getTo(),
                sendTransactionDTO.getValue().toBigInteger(),
                sendTransactionDTO.getData());
        return rpcService.eth_sendTransaction(transaction, sendTransactionDTO.getPass(), contractAddress);
    }

    @RequestMapping(value = "/{contractAddress}/txList", method = RequestMethod.POST)
    private Object txList(@PathVariable String contractAddress, @RequestBody TransactionCountDTO transactionCountDTO) {
        return rpcService.txList(transactionCountDTO.getAddress());
    }

}
