package io.yope.ethereum.rest.resources.sample;

import io.yope.ethereum.model.Method;
import io.yope.ethereum.visitor.BlockchainVisitor;

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
