package org.finki.crypto.puru.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaillierPublicKey {
    private BigInteger n;
    private BigInteger nSquared;
    private BigInteger g;
}
