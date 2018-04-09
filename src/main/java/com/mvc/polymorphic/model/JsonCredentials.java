package com.mvc.polymorphic.model;

import lombok.Data;
import org.web3j.crypto.Credentials;

@Data
public class JsonCredentials {

    private  String publicKey;
    private  String privateKey;
    private  String address;


    public JsonCredentials(Credentials credentials) {
        this.address = credentials.getAddress();
        this.privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
        this.publicKey = credentials.getEcKeyPair().getPublicKey().toString(16);
    }

}
