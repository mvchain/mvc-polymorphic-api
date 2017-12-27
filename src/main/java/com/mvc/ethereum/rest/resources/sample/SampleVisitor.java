package com.mvc.ethereum.rest.resources.sample;

import com.mvc.ethereum.model.Method;
import com.mvc.ethereum.visitor.BlockchainVisitor;

/**
 * Created by enrico.mariotti on 22/05/2016.
 */
public class SampleVisitor extends BlockchainVisitor {

    public SampleVisitor(Method createMethod) {
        super(createMethod);
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
