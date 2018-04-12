package com.mvc.polymorphic.controller;

import com.mvc.polymorphic.model.dto.*;
import com.mvc.polymorphic.service.RpcService;
import com.mvc.polymorphic.utils.FileUtil;
import com.mvc.polymorphic.utils.RSACoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Convert;

import javax.servlet.http.HttpServletRequest;

/**
 * all demo in this controller
 */
@RestController
@RequestMapping("ethereum")
public class EthereumController {

    @Autowired
    private RpcService rpcService;

    /**
     * getBalance
     *
     * @param balanceDTO
     * @return
     */
    @RequestMapping(value = "eth_getBalance", method = RequestMethod.POST)
    public Object eth_getBalance(HttpServletRequest request, @RequestBody final BalanceDTO balanceDTO) throws Exception {
        return rpcService.eth_getBalance(balanceDTO.getAddress(), balanceDTO.getBlockId());
    }

    /**
     * getTransactionByHash
     *
     * @return
     */
    @RequestMapping(value = "eth_getTransactionByHash", method = RequestMethod.POST)
    public Object eth_getTransactionByHash(@RequestBody TransactionByHashDTO transactionByHashDTO) throws Exception {
        return rpcService.eth_getTransactionByHash(transactionByHashDTO.getTransactionHash());
    }

    /**
     * sendRawTransaction
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
     * sendTransaction
     *
     * @param sendTransactionDTO
     * @return
     * @throws Exception
     */
//    @RequestMapping(value = "eth_sendTransaction", method = RequestMethod.POST)
//    public Object eth_sendTransaction(@RequestBody SendTransactionDTO sendTransactionDTO) throws Exception {
//        Transaction transaction = new Transaction(sendTransactionDTO.getFrom(), sendTransactionDTO.getNonce(), sendTransactionDTO.getGasPrice(), sendTransactionDTO.getGas(), sendTransactionDTO.getTo(),
//                Convert.toWei(sendTransactionDTO.getValue(), Convert.Unit.ETHER).toBigInteger(),
//                sendTransactionDTO.getData());
//        return rpcService.eth_sendTransaction(transaction, sendTransactionDTO.getPass());
//    }

    /**
     * search user list
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_listAccounts", method = RequestMethod.POST)
    public Object personal_listAccounts() throws Exception {
        return rpcService.personal_listAccounts();
    }

    /**
     * create new Account
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_newAccount", method = RequestMethod.POST)
    public Object personal_newAccount(@RequestBody NewAccountDTO newAccountDTO) throws Exception {
        return rpcService.personal_newAccount(newAccountDTO.getPassphrase());
    }

    /**
     * import key
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personal_importRawKey", method = RequestMethod.POST)
    public Object personal_importRawKey(@RequestBody ImportRawKeyDTO importRawKeyDTO) throws Exception {
        return rpcService.personal_importRawKey(importRawKeyDTO.getKeydata(), importRawKeyDTO.getPassphrase());
    }

    /**
     * get publickey for RSA
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "publicKey", method = RequestMethod.POST)
    public Object publicKey() throws Exception {
        return RSACoder.getPublicKey();
    }

    /**
     * get TransactionCount for nonce
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "transactionCount", method = RequestMethod.POST)
    public Object getTransactionCount(@RequestBody TransactionCountDTO transactionCountDTO) throws Exception {
        return rpcService.getTransactionCount(transactionCountDTO.getAddress());
    }

    /**
     * personal by keyDate
     * @param file
     * @param passhphrase
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personalByKeyDate", method = RequestMethod.POST)
    public Object eth_personalByKeyDate(@RequestParam("file") MultipartFile file, @RequestParam String passhphrase) throws Exception {
        String source = FileUtil.readFile(file.getInputStream());
        return rpcService.eth_personalByKeyDate(source, passhphrase);
    }

    /**
     * personal by privateKey
     * @param personalByPrivateKeyDTO
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "personalByPrivateKey", method = RequestMethod.POST)
    public Object eth_personalByPrivateKey(@RequestBody  PersonalByPrivateKeyDTO personalByPrivateKeyDTO) throws Exception {
        return rpcService.eth_personalByPrivateKey(personalByPrivateKeyDTO.getPrivateKey());
    }

}
