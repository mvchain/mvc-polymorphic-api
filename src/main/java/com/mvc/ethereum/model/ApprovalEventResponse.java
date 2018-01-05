package com.mvc.ethereum.model;

import lombok.Data;

@Data
public class ApprovalEventResponse {
        private String owner;
        private String spender;
        private long value;

        public ApprovalEventResponse() { }

        public ApprovalEventResponse(
                HumanStandardToken.ApprovalEventResponse approvalEventResponse) {
            this.owner = approvalEventResponse._owner.toString();
            this.spender = approvalEventResponse._spender.toString();
            this.value = approvalEventResponse._value.getValue().longValueExact();
        }
    }