package com.mvc.polymorphic.controller;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.common.BlockResult;
import com.mvc.polymorphic.model.dto.NewAccountDTO;
import com.mvc.polymorphic.model.dto.SendTransactionDTO;
import com.mvc.polymorphic.utils.BlockServiceUtil;
import com.mvc.tools.controller.BaseController;
import com.mvc.tools.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * block controller
 *
 * @author qiyichen
 * @create 2018/4/9 17:13
 */
@RestController
@RequestMapping("/token")
public class BlockController extends BaseController {

    @Autowired
    private BlockChainService blockChainService;

    @GetMapping("/{type}/{address}")
    public Result<BlockResult> getBalance(@PathVariable String type, @PathVariable String address) {
        String serviceName = BlockServiceUtil.getServiceName(type);
        BlockResult result = blockChainService.getBalance(serviceName, address);
        return success(result);
    }

    @GetMapping("/{type}/hash/{hash}")
    public Result<BlockResult> getTransactionByHash(@PathVariable String type, @PathVariable String hash) {
        String serviceName = BlockServiceUtil.getServiceName(type);
        BlockResult result = blockChainService.getTransactionByHash(serviceName, hash);
        return success(result);
    }

    @PostMapping("/{type}/account")
    public Result<BlockResult> newAccount(@PathVariable String type, @Valid @RequestBody NewAccountDTO newAccountDTO) {
        String serviceName = BlockServiceUtil.getServiceName(type);
        BlockResult result = blockChainService.newAccount(serviceName, newAccountDTO.getPassphrase());
        return success(result);
    }

    @PostMapping("/{type}/transaction")
    public Result<BlockResult> sendTransaction(@PathVariable String type, @Valid @RequestBody SendTransactionDTO sendTransactionDTO) {
        String serviceName = BlockServiceUtil.getServiceName(type);
        BlockResult result = blockChainService.sendTransaction(serviceName, sendTransactionDTO.getPass(), sendTransactionDTO.getFrom(), sendTransactionDTO.getTo(), sendTransactionDTO.getValue());
        return success(result);
    }
}
