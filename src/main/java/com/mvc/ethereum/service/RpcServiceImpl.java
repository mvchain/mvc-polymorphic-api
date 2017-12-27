package com.mvc.ethereum.service;

import com.mvc.ethereum.utils.Denomination;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tomcat.util.buf.HexUtils;
import org.ethereum.jsonrpc.JsonRpc;
import org.ethereum.jsonrpc.TypeConverter;
import org.ethereum.net.eth.handler.Eth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;

@Component
public class RpcServiceImpl implements RpcService {

    @Autowired
    private EthereumService ethereumService;

    @Override
    public Object eth_getBalance(String address, String blockId) throws Exception {
        String result = ethereumService.eth_getBalance(address, blockId);
        return Denomination.toFriendlyString(TypeConverter.StringHexToBigInteger(result));
    }

    @Override
    public Object eth_getTransactionByHash(String transactionHash) throws Exception {
        return ethereumService.eth_getTransactionByHash(transactionHash);
    }

    @Override
    public Object eth_sendTransaction(String pass, String from, String to, String gas, String gasPrice, String value, String data, String nonce) throws Exception {
        boolean flag = ethereumService.personal_unlockAccount(from, pass);
        Assert.isTrue(flag, "unlock error");
        JsonRpc.CallArguments callArguments = new JsonRpc.CallArguments();
        callArguments.data = "0x";
        callArguments.from = from;
        callArguments.gas = null ==gas?gas:String.valueOf(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(gas)));
        callArguments.gasPrice = null==gasPrice?gasPrice:String.valueOf(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(gasPrice)));
        callArguments.to = to;
        callArguments.nonce = nonce;
        callArguments.value = TypeConverter.toJsonHex(Denomination.getFriendlyValue(NumberUtils.createBigDecimal(value)).toString(16));
        String result = ethereumService.eth_sendTransaction(callArguments);
        return result;
    }

    @Override
    public Object personal_listAccounts() throws Exception {
        return ethereumService.personal_listAccounts();
    }


}
