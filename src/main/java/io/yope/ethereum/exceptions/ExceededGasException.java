package io.yope.ethereum.exceptions;

public class ExceededGasException extends Throwable {
    public ExceededGasException(String message) {
        super(message);
    }
}
