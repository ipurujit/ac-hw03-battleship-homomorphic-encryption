package org.finki.crypto.puru.model.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedLocation {
    private BigInteger row;
    private BigInteger col;
}
