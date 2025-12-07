package org.finki.crypto.puru.model.components;


import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.finki.crypto.puru.helper.Constants.TOTAL_COLS_PER_BOARD;
import static org.finki.crypto.puru.helper.Constants.TOTAL_ROWS_PER_BOARD;

@Data
public class Board {
    private static final Logger logger = LoggerFactory.getLogger(Board.class);

    private short[][] grid = new short[TOTAL_ROWS_PER_BOARD][TOTAL_COLS_PER_BOARD];

    public void displayNumeric(String name) {
        StringBuilder sb = new StringBuilder("\nBoard of ");
        sb.append(name);
        sb.append(":");
        for (short[] shorts : grid) {
            sb.append("\n");
            for (short aShort : shorts) {
                sb.append(String.format("%2d ", aShort));
            }
        }
        sb.append("\n");
        String output = sb.toString();
        logger.info(output);
    }

    public void display(String name) {
        StringBuilder sb = new StringBuilder("\nBoard of ");
        sb.append(name);
        sb.append(":");
        for (short[] shorts : grid) {
            sb.append("\n");
            for (short aShort : shorts) {
                switch (aShort) {
                    case 0 -> sb.append("_ ");
                    case -1 -> sb.append("X ");
                    default -> sb.append("O ");
                }
            }
        }
        sb.append("\n");
        String output = sb.toString();
        logger.info(output);
    }

}
