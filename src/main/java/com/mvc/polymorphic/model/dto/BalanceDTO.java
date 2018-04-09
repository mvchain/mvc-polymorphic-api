package com.mvc.polymorphic.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class BalanceDTO implements Serializable {
    private String address;
    private String blockId;
}
