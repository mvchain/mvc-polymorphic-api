package com.mvc.ethereum.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;

@Component
@Order(value = 1)
public class MyStartupRunner implements CommandLineRunner {

    @Autowired
    private Web3j web3j;

    @Override
    public void run(String... args) throws Exception {
        web3j.transactionObservable().subscribe(tx -> {
            // do something
        });
    }
}  