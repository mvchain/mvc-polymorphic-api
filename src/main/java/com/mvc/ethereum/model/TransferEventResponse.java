package com.mvc.ethereum.model;

import lombok.Data;
import lombok.Setter;

@Data
public class TransferEventResponse {
    private String from;
    private String to;
    private long value;

    public TransferEventResponse() {
    }

    public TransferEventResponse(
            HumanStandardToken.TransferEventResponse transferEventResponse) {
        this.from = transferEventResponse._from.toString();
        this.to = transferEventResponse._to.toString();
        this.value = transferEventResponse._value.getValue().longValueExact();
    }
}