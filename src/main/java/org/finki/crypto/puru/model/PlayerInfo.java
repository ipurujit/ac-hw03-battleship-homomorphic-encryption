package org.finki.crypto.puru.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finki.crypto.puru.model.components.EncryptedLocation;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInfo {
    private BigInteger blindingFactor;
    private List<EncryptedLocation> encryptedLocations;
    private PaillierPublicKey publicKey;
}
