package org.finki.crypto.puru.model.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int row; // starts from top left of the board as 0 row
    private int col; // from top left as 0 col

    public Location calculateNextLocation(ShipOrientation orientation, int steps) {
        return Location.builder()
                .row(row + (ShipOrientation.VERTICAL.equals(orientation) ? steps : 0))
                .col(col + (ShipOrientation.HORIZONTAL.equals(orientation) ? steps : 0))
                .build();
    }

    public Location copy() {
        return calculateNextLocation(null, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Location l) {
            return row == l.getRow() && col == l.getCol();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }
}
