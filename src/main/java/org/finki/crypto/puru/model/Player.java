package org.finki.crypto.puru.model;

import lombok.Data;
import org.finki.crypto.puru.helper.LoadGameSetupFromDisk;
import org.finki.crypto.puru.helper.LoadKeyFromDisk;
import org.finki.crypto.puru.model.components.Attacks;
import org.finki.crypto.puru.model.components.Board;
import org.finki.crypto.puru.model.components.Ship;
import org.finki.crypto.puru.service.PaillierCrypto;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;

@Data
public class Player {
    private String name;
    private Board board;
    private Attacks attacks;
    private List<Ship> ships;
    private GameSession gameSession;

    private PrivateKey privateKey;
    private PaillierPrivateKey paillierPrivateKey;
    private X509Certificate certificate;

    public Player(String name) {
        this.name = name;
        this.board = LoadGameSetupFromDisk.loadBoard(name);
        this.attacks = LoadGameSetupFromDisk.loadAttacks(name);
        this.ships = LoadGameSetupFromDisk.locateShipsFromBoard(name, board);
        this.gameSession = new GameSession();

        this.privateKey = LoadKeyFromDisk.loadPrivateKey(name);
        this.paillierPrivateKey = PaillierCrypto.generatePaillierPrivateKey(privateKey);
        this.certificate = LoadKeyFromDisk.loadX509Certificate(name);
    }
}
