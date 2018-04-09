package com.mvc.polymorphic.visitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mvc.polymorphic.model.Account;
import com.mvc.polymorphic.model.Method;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public abstract class BlockchainVisitor<T> {

    public BlockchainVisitor(Method createMethod) {
        this.createMethod = createMethod;
    }

    @Getter
    @Setter
    private Method method;

    @Getter
    private Method createMethod;

    @Setter
    @Getter
    private Account account;

    @Getter
    @Setter
    private String address;

    @Setter
    @Getter
    private T model;

    public abstract String getContent();

    public abstract String getName();

    public Object[] getCreateArgs() {
        return createMethod.getArgs();
    }

}
