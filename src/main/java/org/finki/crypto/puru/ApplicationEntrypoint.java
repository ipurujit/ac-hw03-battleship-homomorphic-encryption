package org.finki.crypto.puru;

import org.finki.crypto.puru.helper.Converter;
import org.finki.crypto.puru.model.Player;
import org.finki.crypto.puru.model.PlayerInfo;
import org.finki.crypto.puru.model.Server;
import org.finki.crypto.puru.model.components.EncryptedLocation;
import org.finki.crypto.puru.model.components.GameMode;
import org.finki.crypto.puru.model.messages.DefenderAfterAttack;
import org.finki.crypto.puru.model.messages.ServerToDefender;
import org.finki.crypto.puru.service.GameFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Security;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ApplicationEntrypoint {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationEntrypoint.class);

    public static void start(GameMode mode) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        Scanner scanner = new Scanner(System.in);

        // init both users with boards and attacks read from the files
        Player alice = new Player("alice");
        Player bob = new Player("bob");


        Server server = new Server(
                List.of(Converter.extractPlayerInfo(alice),
                        Converter.extractPlayerInfo(bob))
        );

        // set values in other player
        alice.getGameSession().setOtherPlayerInfo(bob.getName(), bob.getPaillierPrivateKey().getPublicKey());
        bob.getGameSession().setOtherPlayerInfo(alice.getName(), alice.getPaillierPrivateKey().getPublicKey());

        alice.getBoard().display(alice.getName());
        bob.getBoard().display(bob.getName());

        alice.getBoard().displayNumeric(alice.getName());
        bob.getBoard().displayNumeric(bob.getName());

        List<Player> players = List.of(alice, bob);
        int currentTurn = 0;

        while (!alice.getGameSession().isWinner() && !bob.getGameSession().isWinner() &&
                players.get(currentTurn).getGameSession().getCurrentAttack() <
                        players.get(currentTurn).getAttacks().getLocations().size()) {
            // we keep playing
            Player attacker = players.get(currentTurn);
            currentTurn = (currentTurn + 1) % players.size();
            Player defender = players.get(currentTurn);
            PlayerInfo defenderInfo = server.getPlayerInfoList().get(currentTurn);
            EncryptedLocation encryptedTarget = GameFlow.attackOtherPlayer(attacker, defender, mode, scanner);
            ServerToDefender serverToDefender = GameFlow.processEncryptedAttackAtServer(defenderInfo, encryptedTarget);
            DefenderAfterAttack defenderAfterAttack = GameFlow.processAttackAtDefender(
                    defender, serverToDefender
            );
            GameFlow.processDefenderResponse(attacker, defenderAfterAttack);
        }
        logger.info("========= GAME OVER ==========");
        Optional<Player> winner = players.stream().filter(player -> player.getGameSession().isWinner())
                .findFirst()
                .map(player -> {
                    logger.info("THE WINNER IS {}", player.getName());
                    return player;
                });
        if (winner.isEmpty()) {
            logger.info("THE GAME ENDED BECAUSE ONE OF THE PLAYERS HAS NO MORE ATTACKS LEFT!");
        }
        logger.info("{} attack index {}, attack list size {}, ships lost {}",
                alice.getName(),
                alice.getGameSession().getCurrentAttack(),
                alice.getAttacks().getLocations().size(),
                alice.getGameSession().getSunkShipCount());
        logger.info("{} attack index {}, attack list size {}, ships lost {}",
                bob.getName(),
                bob.getGameSession().getCurrentAttack(),
                bob.getAttacks().getLocations().size(),
                bob.getGameSession().getSunkShipCount());

        scanner.close();
    }

    public static void main(String[] args) {
        start(GameMode.AUTO); // or GameMode.INTERACTIVE
    }
}