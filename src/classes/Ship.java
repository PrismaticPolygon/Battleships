package classes;

import enums.ShipStatus;
import exceptions.InvalidPositionException;
import interfaces.ShipInterface;

import java.util.Arrays;

public class Ship implements ShipInterface {

    private ShipStatus statuses[];

    Ship(int size) {

        this.statuses = new ShipStatus[size];
        Arrays.fill(this.statuses, ShipStatus.INTACT);

    }

    Ship(ShipStatus[] statuses) {
        this.statuses = statuses;
    }

    public int getSize() {
        return this.statuses.length;
    }

    /**
     * @return true if and only if every cell of the ship is SUNK
     */
    public boolean isSunk() {

        for (ShipStatus s : this.statuses) {

            if (s != ShipStatus.SUNK) return false;

        }

        return true;

    }

    /**
     * Change a cell of the ship as specified by offset to HIT. If all cells are HIT, update all to SUNK
     *
     * @param offset The offset from the top/left of the ship
     *
     * @throws InvalidPositionException if the offset is negative or greater than the size of the ship
     */
    public void shoot(int offset) throws InvalidPositionException {

        if (offset < 0 || offset >= this.getSize()) throw new InvalidPositionException("Invalid offset: " + offset);

        this.statuses[offset] = ShipStatus.HIT;

        for (ShipStatus s: this.statuses) {

            if (s == ShipStatus.INTACT) return;

        }

        Arrays.fill(this.statuses, ShipStatus.SUNK);

    }

    /**
     * @param offset The offset from the top/left of the ship.
     *
     * @return the status of the specified cell
     *
     * @throws InvalidPositionException if the offset is negative or greater than the size of the ship
     */
    public ShipStatus getStatus(int offset) throws InvalidPositionException {

        if (offset < 0 || offset >= this.getSize()) throw new InvalidPositionException();

        return this.statuses[offset];

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.statuses.length; i++) {

            sb.append(this.statuses[i].toString());

            if (i != this.statuses.length - 1) sb.append(",");

        }

        return sb.toString();

    }

}
