package org.finki.crypto.puru.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finki.crypto.puru.service.PaillierCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaillierPrivateKey {
    private static final Logger logger = LoggerFactory.getLogger(PaillierPrivateKey.class);


    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger nSquared;
    private BigInteger g;
    private BigInteger lambda;
    private BigInteger mu;

    public void initialize(BigInteger p, BigInteger q, BigInteger n, BigInteger g) {
        this.p = p;
        this.q = q;
        this.n = n;
        this.nSquared = n.multiply(n);
        this.g = g;
        this.lambda = PaillierCrypto.calculateLambda(p, q);
        // we create a separate initialize because we are passing this object to calculate Mu
        this.mu = PaillierCrypto.calculateMu(this);
    }

    public PaillierPublicKey getPublicKey() {
        return PaillierPublicKey.builder().n(n).nSquared(nSquared).g(g).build();
    }

    public void display() {
        String string = "Paillier Private Key :\np=" + p.toString(16) +
                "\n q=" + q.toString(16) +
                "\n n=" + n.toString(16) +
                "\n nSquared=" + nSquared.toString(16) +
                "\n g=" + g.toString(16) +
                "\n lambda=" + lambda.toString(16) +
                "\n mu=" + mu.toString(16);
        logger.info(string);
    }
}
