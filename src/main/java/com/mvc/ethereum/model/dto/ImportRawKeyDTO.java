package com.mvc.ethereum.model.dto;


import lombok.Data;

@Data
public class ImportRawKeyDTO {

    private String keydata;
    private String passphrase;
}
