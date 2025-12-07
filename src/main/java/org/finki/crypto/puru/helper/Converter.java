package org.finki.crypto.puru.helper;

import lombok.NoArgsConstructor;
import org.finki.crypto.puru.model.Player;
import org.finki.crypto.puru.model.PlayerInfo;
import org.finki.crypto.puru.model.components.EncryptedLocation;
import org.finki.crypto.puru.model.components.Location;
import org.finki.crypto.puru.model.components.Ship;
import org.finki.crypto.puru.service.PaillierCrypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class Converter {
    public static List<Location> convertShipToLocations(Ship ship) {
        List<Location> locations = new ArrayList<>();
        Location curr = ship.getStartLocation().copy();
        int len = ship.getLength();
        while (len-- > 0) {
            locations.add(curr);
            curr = curr.calculateNextLocation(ship.getOrientation(), 1);
        }
        return locations;
    }

    public static PlayerInfo extractPlayerInfo(Player player) {
        BigInteger blindingFactor = PaillierCrypto.generateRandomR(player.getPaillierPrivateKey().getN());
        return PlayerInfo.builder()
                .blindingFactor(blindingFactor)
                .encryptedLocations(player.getShips().stream()
                        .flatMap(ship -> Converter.convertShipToLocations(ship).stream())
                        .map(loc -> new EncryptedLocation(
                                PaillierCrypto.getEncryptLocationCoordinate(player.getPaillierPrivateKey().getPublicKey(), loc.getRow()),
                                PaillierCrypto.getEncryptLocationCoordinate(player.getPaillierPrivateKey().getPublicKey(), loc.getCol())
                        ))
                        .toList())
                .publicKey(player.getPaillierPrivateKey().getPublicKey())
                .build();
    }
}
