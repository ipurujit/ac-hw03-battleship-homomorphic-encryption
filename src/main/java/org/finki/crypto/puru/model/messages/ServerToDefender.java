package org.finki.crypto.puru.model.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.finki.crypto.puru.model.components.EncryptedLocation;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerToDefender {
    private EncryptedLocation encryptedTarget;
    private List<EncryptedLocation> encryptedBlindedDifferenceList;
}

