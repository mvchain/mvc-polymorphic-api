package com.mvc.ethereum.model.dto;

import lombok.Data;

@Data
public class SendTransactionDTO {

    private String pass;
    private String from;
    private String to;
    private String gas;
    private String gasPrice;
    private String value;
    private String data;
    private String nonce;

}