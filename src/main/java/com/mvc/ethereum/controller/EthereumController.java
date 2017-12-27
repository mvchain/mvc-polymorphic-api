package com.mvc.ethereum.controller;

import com.mvc.ethereum.model.dto.BalanceDTO;
import com.mvc.ethereum.model.dto.SendTransactionDTO;
import com.mvc.ethereum.model.dto.TransactionByHashDTO;
import com.mvc.ethereum.service.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 目前demoe先所有放在一起
 */
@RestController
@RequestMapping("ethereum")
public class EthereumController {

    @Autowired
    private RpcService rpcService;

    /**
     * 查询余额
     *
     * @param balanceDTO
     * @return
     */
    @RequestMapping(value = "eth_getBalance", method = RequestMethod.POST)
    public Object eth_getBalance(@RequestBody final BalanceDTO balanceDTO) throws Exception {
        return rpcService.eth_getBalance(balanceDTO.getAddress(), balanceDTO.getBlockId());
    }

    /**
     * 根据hash查询订单信息
     *
     * @return
     */
    @RequestMapping(value = "eth_getTransactionByHash", method = RequestMethod.POST)
    public Object eth_getTransactionByHash(@RequestBody TransactionByHashDTO transactionByHashDTO) throws Exception {
        return rpcService.eth_getTransactionByHash(transactionByHashDTO.getTransactionHash());
    }


    /**
     * 发起事物
     * @param sendTransactionDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "eth_sendTransaction", method = RequestMethod.POST)
    public Object eth_sendTransaction(@RequestBody SendTransactionDTO sendTransactionDTO) throws Exception {
        return rpcService.eth_sendTransaction(sendTransactionDTO.getPass(), sendTransactionDTO.getFrom(), sendTransactionDTO.getTo(), sendTransactionDTO.getGas(), sendTransactionDTO.getGasPrice(), sendTransactionDTO.getValue(), sendTransactionDTO.getData(), sendTransactionDTO.getNonce());
    }

    @RequestMapping(value = "personal_listAccounts", method = RequestMethod.POST)
    public Object personal_listAccounts() throws Exception {
        return rpcService.personal_listAccounts();
    }


}
