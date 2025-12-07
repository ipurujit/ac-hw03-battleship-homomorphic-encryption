package org.finki.crypto.puru.exception;

public class X509CertificateLoadException extends RuntimeException {

    public X509CertificateLoadException(String message, Exception cause) {
        super(message, cause);
    }
}
