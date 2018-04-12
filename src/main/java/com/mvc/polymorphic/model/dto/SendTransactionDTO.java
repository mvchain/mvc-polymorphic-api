package com.mvc.polymorphic.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ethands
 */
@Data
public class SendTransactionDTO implements Serializable {
    private static final long serialVersionUID = 6477321453043666156L;
    @NotNull
    private String pass;
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private BigDecimal value;

}