package org.finki.crypto.puru.service;

import lombok.NoArgsConstructor;
import org.finki.crypto.puru.model.Player;
import org.finki.crypto.puru.model.PlayerInfo;
import org.finki.crypto.puru.model.components.EncryptedLocation;
import org.finki.crypto.puru.model.components.GameMode;
import org.finki.crypto.puru.model.components.Location;
import org.finki.crypto.puru.model.messages.DefenderAfterAttack;
import org.finki.crypto.puru.model.messages.ServerToDefender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class GameFlow {
    private static final Logger logger = LoggerFactory.getLogger(GameFlow.class);

    public static Location getNextAttack(Player attacker, GameMode mode, Scanner scanner) {
        if (GameMode.AUTO.equals(mode)) {
            return attacker.getAttacks().getLocations().get(
                    attacker.getGameSession().getAndIncrementCurrentAttack()
            );
        }
        // interactive
        Location target = null;
        do {
            if (target != null) {
                logger.info("Your selected target {} was already provided before! Provide a different one!", target);
            }
            logger.info("\nProvide target location where player {} will attack player {}: <row> <col> :",
                    attacker.getName(), attacker.getGameSession().getOtherPlayerName());
            int row = scanner.nextInt();
            int col = scanner.nextInt();
            scanner.nextLine();
            target = new Location(row, col);
        } while (attacker.getGameSession().getSuccessfulAttacks().getLocations().contains(target) ||
                attacker.getGameSession().getFailedAttacks().getLocations().contains(target));
        return target;
    }

    public static EncryptedLocation attackOtherPlayer(Player attacker, Player other, GameMode mode, Scanner scanner) {
        Location target = getNextAttack(attacker, mode, scanner);
        logger.info("Player {} attacks {} at the location {}",
                attacker.getName(), other.getName(), target);
        attacker.getGameSession().setLastTarget(target);
        return new EncryptedLocation(
                PaillierCrypto.getEncryptLocationCoordinate(attacker.getGameSession().getOtherPlayerPublicKey(), target.getRow()),
                PaillierCrypto.getEncryptLocationCoordinate(attacker.getGameSession().getOtherPlayerPublicKey(), target.getCol())
        );
    }

    private static BigInteger getBlindedCoordinateDifference(PlayerInfo playerInfo, BigInteger encryptedPoint, BigInteger encryptedTargetPoint) {
        BigInteger res = PaillierCrypto.subtract(encryptedPoint, encryptedTargetPoint, playerInfo.getPublicKey());
        return PaillierCrypto.multiplyByScalar(res, playerInfo.getBlindingFactor(), playerInfo.getPublicKey());
    }

    public static ServerToDefender processEncryptedAttackAtServer(PlayerInfo playerInfo, EncryptedLocation encryptedTarget) {
        logger.info("Server multiplies encrypted target with blinding factor");
        List<EncryptedLocation> encryptedBlindedDifferenceList = playerInfo.getEncryptedLocations().stream()
                .map(loc -> new EncryptedLocation(
                        getBlindedCoordinateDifference(playerInfo, loc.getRow(), encryptedTarget.getRow()),
                        getBlindedCoordinateDifference(playerInfo, loc.getCol(), encryptedTarget.getCol())
                ))
                .toList();
        return ServerToDefender.builder()
                .encryptedTarget(encryptedTarget)
                .encryptedBlindedDifferenceList(encryptedBlindedDifferenceList)
                .build();
    }

    public static DefenderAfterAttack processAttackAtDefender(Player defender, ServerToDefender serverToDefender) {
        logger.info("Player {} decrypts the blinded differences", defender.getName());
        Location target = new Location(
                PaillierCrypto.decrypt(serverToDefender.getEncryptedTarget().getRow(), defender.getPaillierPrivateKey()).intValue(),
                PaillierCrypto.decrypt(serverToDefender.getEncryptedTarget().getCol(), defender.getPaillierPrivateKey()).intValue()
        );
        logger.info("Player {} decrypts target {}", defender.getName(), target);
        defender.getGameSession().getOtherPlayerAttacks().getLocations().add(target);
        List<Location> decryptedDifferences = serverToDefender.getEncryptedBlindedDifferenceList().stream()
                .map(loc -> new Location(
                        PaillierCrypto.decrypt(loc.getRow(), defender.getPaillierPrivateKey()).intValue(),
                        PaillierCrypto.decrypt(loc.getCol(), defender.getPaillierPrivateKey()).intValue()
                ))
                .toList();
        DefenderAfterAttack defenderAfterAttack = new DefenderAfterAttack();
        defenderAfterAttack.setHit(decryptedDifferences.stream()
                .anyMatch(dec -> dec.equals(new Location(0, 0))));
        short shipId = defender.getBoard().getGrid()[target.getRow()][target.getCol()];
        defender.getBoard().getGrid()[target.getRow()][target.getCol()] = -1; // mark as attacked
        if (defenderAfterAttack.isHit()) {
            // update the ship details
            defender.getShips().stream().filter(ship -> ship.getId() == shipId)
                    .forEach(ship -> {
                        if (ship.checkWhetherHit(target) && ship.isSunk()) {
                            logger.info("Player {} caused ship {} of {} to sink!",
                                    defender.getGameSession().getOtherPlayerName(),
                                    shipId,
                                    defender.getName());
                            defender.getGameSession().incrementSunkShipCount();
                        }
                        if (defender.getGameSession().getSunkShipCount() == defender.getShips().size()) {
                            logger.info("Player {} caused ALL SHIPS OF {} to sink!",
                                    defender.getGameSession().getOtherPlayerName(),
                                    defender.getName());
                            // game over!
                            defenderAfterAttack.setGameOver(true);
                            defender.getBoard().display(defender.getName());
                        }
                    });
        }
        return defenderAfterAttack;
    }

    public static void processDefenderResponse(Player attacker, DefenderAfterAttack defenderAfterAttack) {
        if (defenderAfterAttack.isHit()) {
            logger.info("Player {} has HIT a ship of {}!", attacker.getName(), attacker.getGameSession().getOtherPlayerName());
            attacker.getGameSession().getSuccessfulAttacks().getLocations().add(
                    attacker.getGameSession().getLastTarget()
            );
        } else {
            logger.info("Player {} has MISSED!", attacker.getName());
            attacker.getGameSession().getFailedAttacks().getLocations().add(
                    attacker.getGameSession().getLastTarget()
            );
        }
        if (defenderAfterAttack.isGameOver()) {
            attacker.getGameSession().setWinner(true);
            logger.info("Player {} has won by defeating {}", attacker.getName(), attacker.getGameSession().getOtherPlayerName());
        } else {
            logger.info("The game continues. Now {} will attack.", attacker.getGameSession().getOtherPlayerName());
        }
    }


}
