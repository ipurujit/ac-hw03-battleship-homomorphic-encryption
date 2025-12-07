package org.finki.crypto.puru.model.components;

import lombok.Data;

import static org.finki.crypto.puru.helper.Constants.MAX_LENGTH_PER_SHIP;

@Data
public class Ship {
    private short id; // to help identify on the board
    private Location startLocation;
    private ShipOrientation orientation; // HORIZONTAL for ROW, and VERTICAL for COL
    private int length;
    private boolean[] hitFlag = new boolean[MAX_LENGTH_PER_SHIP];

    public Ship(short id, Location startLocation, ShipOrientation orientation, int length) {
        this.id = id;
        this.startLocation = startLocation;
        this.orientation = orientation;
        this.length = length;
    }

    public void incrementLength() {
        length++;
    }

    public boolean checkWhetherHit(Location targetLocation) {
        if (length < 1) {
            return false;
        }
        for (int step = 0; step < length; step++) {
            if (startLocation.calculateNextLocation(orientation, step).equals(targetLocation)) {
                // hit!
                // set hit and then move
                hitFlag[step] = true;
                return true;
            }
        }
        return false;
    }

    public boolean isSunk() {
        for (int i = 0; i < length; i++) {
            if (!hitFlag[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\nShip id=");
        sb.append(id);
        sb.append(",Orientation=");
        sb.append(orientation);
        sb.append(",Length=");
        sb.append(length);
        sb.append(",");
        sb.append(startLocation);
        for (int step = 1; step < length; step++) {
            sb.append(", ").append(startLocation.calculateNextLocation(orientation, step));
        }
        return sb.toString();
    }
}
