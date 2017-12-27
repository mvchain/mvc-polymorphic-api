package com.mvc.ethereum.rpc;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.mvc.ethereum.service.EthereumService;
import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EthereumResource {

    private JsonRpcHttpClient client;
    private EthereumRpc ethereumRpc;
    private EthereumService jsonRpc;


    public EthereumResource(final String ethereumURL) throws MalformedURLException {
        this.client = new JsonRpcHttpClient(new URL(ethereumURL));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        client.setHeaders(headers);
        client.setConnectionTimeoutMillis(1000);
        client.setReadTimeoutMillis(1000);
    }

    public EthereumRpc getGethRpc() {
        if (ethereumRpc == null) {
            this.ethereumRpc =
                    ProxyUtil.createClientProxy(getClass().getClassLoader(), EthereumRpc.class, client);
        }
        return this.ethereumRpc;
    }

    public EthereumService getRpc() {
        if (jsonRpc == null) {
            this.jsonRpc =
                    ProxyUtil.createClientProxy(getClass().getClassLoader(), EthereumService.class, client);
        }
        return this.jsonRpc;
    }
}
