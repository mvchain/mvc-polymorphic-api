package com.mvc.ethereum.model.dto;

import lombok.Data;

@Data
public class ExportAccountDTO {
    private String address;
    private String passphrase;
}
