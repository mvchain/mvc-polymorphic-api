package com.mvc.ethereum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvc.ethereum.model.dto.*;
import com.mvc.ethereum.service.RpcService;
import com.mvc.ethereum.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.*;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.utils.Convert;

import javax.servlet.http.HttpServletRequest;

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
    public Object eth_getBalance(HttpServletRequest request, @RequestBody final BalanceDTO balanceDTO) throws Exception {
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
     * 发起已签名事物
     *
     * @param rawTransactionDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "eth_sendRawTransaction", method = RequestMethod.POST)
    public Object ethSendRawTransaction(@RequestBody RawTransactionDTO rawTransactionDTO) throws Exception {
        return rpcService.ethSendRawTransaction(rawTransactionDTO.getSignedMessage());
    }

    /**
     * 发起事物
     *
     * @param sendTransactionDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "eth_sendTransaction", method = RequestMethod.POST)
    public Object eth_sendTransaction(@RequestBody SendTransactionDTO sendTransactionDTO) throws Exception {
        Transaction transaction = new Transaction(sendTransactionDTO.getFrom(), sendTransactionDTO.getNonce(), sendTransactionDTO.getGasPrice(), sendTransactionDTO.getGas(), sendTransactionDTO.getTo(),
                Convert.toWei(sendTransactionDTO.getValue(), Convert.Unit.ETHER).toBigInteger(),
                sendTransactionDTO.getData());
        return rpcService.eth_sendTransaction(transaction, sendTransactionDTO.getPass());
    }

    /**
     * 查询账号列表
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_listAccounts", method = RequestMethod.POST)
    public Object personal_listAccounts() throws Exception {
        return rpcService.personal_listAccounts();
    }

    /**
     * 创建账户
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_newAccount", method = RequestMethod.POST)
    public Object personal_newAccount(@RequestBody NewAccountDTO newAccountDTO) throws Exception {
        return rpcService.personal_newAccount(newAccountDTO.getPassphrase());
    }

    /**
     * 导入key
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_importRawKey", method = RequestMethod.POST)
    public Object personal_importRawKey(@RequestBody ImportRawKeyDTO importRawKeyDTO) throws Exception {
        return rpcService.personal_importRawKey(importRawKeyDTO.getKeydata(), importRawKeyDTO.getPassphrase());
    }

    /**
     * 导出key
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "parity_ExportAccount", method = RequestMethod.POST)
    public Object parityExportAccount(@RequestBody ExportAccountDTO exportAccountDTO) throws Exception {
        return rpcService.parityExportAccount(exportAccountDTO.getAddress(), exportAccountDTO.getPassphrase());
    }

    /**
     * 获取公钥
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "publicKey", method = RequestMethod.POST)
    public Object publicKey() throws Exception {
        return RSACoder.getPublicKey();
    }

}
