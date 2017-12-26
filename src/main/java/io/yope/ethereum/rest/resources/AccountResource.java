package io.yope.ethereum.rest.resources;

import io.yope.ethereum.model.Account;
import io.yope.ethereum.rpc.services.EthereumFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountResource {

    @Autowired
    private EthereumFacade facade;

    /**
     * 查询个人账户
     * @param address
     * @return
     */
    @RequestMapping(value = "/{address}", method = RequestMethod.GET)
    public @ResponseBody
    EthereumResponse<Account> getAccount(@PathVariable final String address) {
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
