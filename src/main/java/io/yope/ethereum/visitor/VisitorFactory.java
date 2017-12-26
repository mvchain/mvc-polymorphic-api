package io.yope.ethereum.visitor;

import io.yope.ethereum.model.Account;
import io.yope.ethereum.model.Method;

import java.util.Optional;

/**
 * Contract factory.
 */
public class VisitorFactory {

    public static Method buildMethod(final Optional<String> name, final Optional<Object[]> args) {
        return Method.builder().name(name.isPresent() ? name.get() : null).args(args.isPresent() ? args.get() : null).build();
    }

    public static<T> BlockchainVisitor build(final String contractAddress, final String accountAddress, final String pwd, final String name, final String content, final T model, Method method) {
        Account account = Account.builder().address(accountAddress).passphrase(pwd).build();

        BlockchainVisitor visitor = new BlockchainVisitor(method) {

            @Override
            public String getContent() {
                return content;
            }

            @Override
            public String getName() {
                return name;
            }
        };
        visitor.setAccount(account);
        visitor.setModel(model);
        visitor.setAddress(contractAddress);
        return  visitor;
    }
}
