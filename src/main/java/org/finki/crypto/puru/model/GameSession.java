package org.finki.crypto.puru.model;

import lombok.Data;
import org.finki.crypto.puru.model.components.Attacks;
import org.finki.crypto.puru.model.components.Location;

@Data
public class GameSession {
    private boolean isWinner = false;
    private int currentAttack = 0;
    private int sunkShipCount = 0;
    // how many of own attacks were successful/failed
    private Attacks successfulAttacks = new Attacks();
    private Attacks failedAttacks = new Attacks();
    // how many attacks the other player made
    private Attacks otherPlayerAttacks = new Attacks();

    private Location lastTarget;

    private String otherPlayerName;
    private PaillierPublicKey otherPlayerPublicKey;

    public void setOtherPlayerInfo(String name, PaillierPublicKey publicKey) {
        this.otherPlayerName = name;
        this.otherPlayerPublicKey = publicKey;
    }

    public int getAndIncrementCurrentAttack() {
        return currentAttack++;
    }

    public void incrementSunkShipCount() {
        sunkShipCount++;
    }
}
