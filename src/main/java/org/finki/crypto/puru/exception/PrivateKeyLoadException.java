package org.finki.crypto.puru.exception;

public class PrivateKeyLoadException extends RuntimeException {

    public PrivateKeyLoadException(String message, Exception cause) {
        super(message, cause);
    }
}
