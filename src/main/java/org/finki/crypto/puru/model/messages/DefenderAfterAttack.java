package org.finki.crypto.puru.model.messages;

import lombok.Data;
import org.finki.crypto.puru.model.components.Location;

import java.util.List;

@Data
public class DefenderAfterAttack {
    private boolean hit = false;
    private boolean isGameOver = false; // all ships sank
    private List<Location> decryptedDifferences;
}

