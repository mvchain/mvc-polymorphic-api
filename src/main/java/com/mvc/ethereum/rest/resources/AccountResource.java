package com.mvc.ethereum.rest.resources;

import com.mvc.ethereum.model.Account;
import com.mvc.ethereum.rpc.services.EthereumFacade;
import com.mvc.ethereum.service.EthereumService;
import com.mvc.ethereum.utils.Denomination;
import org.ethereum.jsonrpc.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@Deprecated
public class AccountResource {

    @Autowired
    private EthereumFacade facade;
    @Autowired
    private EthereumService jsonRpc;


    /**
     * 查询个人账户
     * @param address
     * @return
     */
    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    public @ResponseBody
    EthereumResponse<Account> getAccount(@PathVariable final String address) {
        String result = null;
        try {
            result = jsonRpc.eth_getBalance(address, "latest");
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(Denomination.toFriendlyString(TypeConverter.StringHexToBigInteger(result)));
        return new EthereumResponse<Account>(facade.getAccount(address), 200, "OK");
    }

    /**
     * 创建账户
     * @param account
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody
    EthereumResponse<Account> createAccount(@RequestBody final Account account) {
        return new EthereumResponse<Account>(facade.createAccount(account.getPassphrase()), 200, "OK");
    }

    /**
     * 解锁账户
     * @param account
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public @ResponseBody
    EthereumResponse<Boolean> unlockAccount(@RequestBody final Account account) {
        return new EthereumResponse<Boolean>(facade.unlockAccount(account), 200, "OK");
    }

}
