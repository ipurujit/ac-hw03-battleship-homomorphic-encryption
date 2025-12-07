package org.finki.crypto.puru.helper;


import lombok.NoArgsConstructor;
import org.finki.crypto.puru.exception.AttacksLoadException;
import org.finki.crypto.puru.exception.BoardLoadException;
import org.finki.crypto.puru.exception.ShipLoadException;
import org.finki.crypto.puru.model.components.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static org.finki.crypto.puru.helper.Constants.*;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class LoadGameSetupFromDisk {
    private static final Logger logger = LoggerFactory.getLogger(LoadGameSetupFromDisk.class);


    public static Board loadBoard(String name) {
        Board board = new Board();
        try (InputStream input = LoadGameSetupFromDisk.class.getResourceAsStream(String.format("/game_setup/%s/board.txt", name))) {
            try (Scanner scanner = new Scanner(Objects.requireNonNull(input))) {
                for (int i = 0; i < TOTAL_ROWS_PER_BOARD; i++) {
                    for (int j = 0; j < TOTAL_COLS_PER_BOARD; j++) {
                        board.getGrid()[i][j] = scanner.nextShort();
                    }
                }
            }
        } catch (IOException e) {
            throw new BoardLoadException("Failed to read the board", e);
        }
        return board;
    }

    public static List<Ship> locateShipsFromBoard(String name, Board board) {
        List<Ship> ships = new ArrayList<>();
        for (int i = 0; i < TOTAL_ROWS_PER_BOARD; i++) {
            for (int j = 0; j < TOTAL_COLS_PER_BOARD; j++) {
                short val = board.getGrid()[i][j];
                if (val != 0) {
                    processCurrentBoardLocation(name, i, j, ships, val);
                }
            }
        }
        logger.info("Loaded the ships for {}: {}", name, ships);
        if (ships.size() > TOTAL_SHIPS_PER_PLAYER) {
            throw new ShipLoadException(
                    String.format("Number of ships %d higher than allowed limit %d", ships.size(), TOTAL_SHIPS_PER_PLAYER),
                    null);
        }
        return ships;
    }

    private static void processCurrentBoardLocation(String name, int i, int j, List<Ship> ships, short val) {
        // ship!
        Location currLocation = new Location(i, j);
        Ship ship = ships.stream()
                .filter(s -> val == s.getId())
                .findFirst()
                .orElseGet(() -> new Ship(val, currLocation, null, 0));
        if (ship.getLength() == 0) {
            // for the 1st time we see this ship
            ships.add(ship);
        } else if (ship.getOrientation() == null) {
            // for the 2nd time we see this ship
            // we need to find the orientation
            calculateAndSetOrientation(name, i, j, val, ship);
        } else if (!currLocation.equals(ship.getStartLocation()
                .calculateNextLocation(ship.getOrientation(), ship.getLength()))) {
            // we need to verify the orientation and linear continuity
            throw new ShipLoadException(String.format(
                    "Ship %d for player %s must be continuous on the board", val, name), null);
        } else if (ship.getLength() > MAX_LENGTH_PER_SHIP) {
            throw new ShipLoadException(String.format(
                    "Ship length %d higher than the allowed limit %d", ship.getLength(), MAX_LENGTH_PER_SHIP), null);
        }
        ship.incrementLength();
    }

    private static void calculateAndSetOrientation(String name, int i, int j, short val, Ship ship) {
        if (ship.getStartLocation().getRow() == i - 1 && ship.getStartLocation().getCol() == j) {
            // same col, but row increment = VERTICAL
            ship.setOrientation(ShipOrientation.VERTICAL);
        } else if (ship.getStartLocation().getRow() == i && ship.getStartLocation().getCol() == j - 1) {
            // same row, but col increment = HORIZONTAL
            ship.setOrientation(ShipOrientation.HORIZONTAL);
        } else {
            throw new ShipLoadException(String.format(
                    "Ship %d for player %s orientation must be horizontal or vertical!", val, name
            ), null);
        }
    }

    public static Attacks loadAttacks(String name) {
        Attacks attacks = new Attacks();
        int row;
        try (InputStream input = LoadGameSetupFromDisk.class.getResourceAsStream(String.format("/game_setup/%s/attacks.txt", name))) {
            try (Scanner scanner = new Scanner(Objects.requireNonNull(input))) {
                while (scanner.hasNextInt()) {
                    row = scanner.nextInt();
                    if (!scanner.hasNextInt()) {
                        throw new AttacksLoadException("Mismatch between attack rows and columns", null);
                    }
                    attacks.getLocations().add(new Location(row, scanner.nextInt()));
                }
            }
        } catch (IOException e) {
            throw new AttacksLoadException("Failed to read the attacks", e);
        }
        return attacks;
    }


}
