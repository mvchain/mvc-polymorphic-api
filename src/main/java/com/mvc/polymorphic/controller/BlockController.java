package com.mvc.polymorphic.controller;

import com.mvc.polymorphic.common.BlockChainService;
import com.mvc.polymorphic.utils.BlockServiceUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * block controller
 *
 * @author qiyichen
 * @create 2018/4/9 17:13
 */
@RestController
@RequestMapping("/token")
public class BlockController {

    @Autowired
    private BlockChainService blockChainService;

    @GetMapping("/{type}/{address}")
    public Object getBalance(@PathVariable String type, @PathVariable String address) {
        String serviceName = BlockServiceUtil.getServiceName(type);
        return blockChainService.getBalance(serviceName, address);
    }
}
