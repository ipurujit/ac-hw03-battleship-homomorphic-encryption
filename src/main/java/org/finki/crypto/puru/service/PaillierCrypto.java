package org.finki.crypto.puru.service;

import lombok.NoArgsConstructor;
import org.finki.crypto.puru.helper.Constants;
import org.finki.crypto.puru.model.PaillierPrivateKey;
import org.finki.crypto.puru.model.PaillierPublicKey;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class PaillierCrypto {
    public static PaillierPrivateKey generatePaillierPrivateKey(PrivateKey privateKey) {
        PaillierPrivateKey paillierPrivateKey = new PaillierPrivateKey();
        if (privateKey instanceof RSAPrivateCrtKey rsaprivateCrtKey) {
            paillierPrivateKey.initialize(
                    rsaprivateCrtKey.getPrimeP(),
                    rsaprivateCrtKey.getPrimeQ(),
                    rsaprivateCrtKey.getModulus(),
                    rsaprivateCrtKey.getModulus().add(BigInteger.ONE)
            );
        } else {
            throw new IllegalArgumentException("Private key is not a RSAPrivateCrtKey");
        }
        return paillierPrivateKey;
    }

    private static BigInteger calculateLFunc(BigInteger x, BigInteger n) {
        return x.subtract(BigInteger.ONE).divide(n);
    }

    private static BigInteger calculateLCM(BigInteger p, BigInteger q) {
        return p.multiply(q).divide(p.gcd(q));
    }

    public static BigInteger calculateLambda(BigInteger p, BigInteger q) {
        return calculateLCM(p.subtract(BigInteger.ONE), q.subtract(BigInteger.ONE));
    }

    public static BigInteger calculateMu(PaillierPrivateKey privateKey) {
        return calculateLFunc(
                privateKey.getG().modPow(privateKey.getLambda(), privateKey.getNSquared()),
                privateKey.getN()
        ).modInverse(privateKey.getN());
    }

    public static BigInteger generateRandomR(BigInteger n) {
        return new BigInteger(n.bitLength(), Constants.SECURE_RANDOM).mod(n);
    }

    public static BigInteger encrypt(BigInteger m, PaillierPublicKey pubKey) {
        return pubKey.getG().modPow(m, pubKey.getNSquared())
                .multiply(generateRandomR(pubKey.getN()).modPow(pubKey.getN(), pubKey.getNSquared()))
                .mod(pubKey.getNSquared());
    }

    public static BigInteger decrypt(BigInteger c, PaillierPrivateKey privateKey) {
        return calculateLFunc(c.modPow(privateKey.getLambda(), privateKey.getNSquared()), privateKey.getN())
                .multiply(privateKey.getMu())
                .mod(privateKey.getN());
    }

    public static BigInteger subtract(BigInteger c1, BigInteger c2, PaillierPublicKey pubKey) {
        return c1.multiply(c2.modInverse(pubKey.getNSquared())).mod(pubKey.getNSquared());
    }

    public static BigInteger multiplyByScalar(BigInteger c, BigInteger s, PaillierPublicKey pubKey) {
        return c.modPow(s, pubKey.getNSquared());
    }

    public static BigInteger getEncryptLocationCoordinate(PaillierPublicKey publicKey, int point) {
        return encrypt(
                BigInteger.valueOf(point),
                publicKey
        );
    }
}
