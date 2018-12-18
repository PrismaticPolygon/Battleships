package classes;

import enums.ShipStatus;
import exceptions.InvalidPositionException;
import exceptions.ShipOverlapException;
import interfaces.BoardInterface;
import interfaces.ShipInterface;

import java.util.HashMap;
import java.util.Map;

public class Board implements BoardInterface {

    private HashMap<Placement, ShipInterface> ships = new HashMap<>();

    /**
     * @param position A position to be validated
     *
     * @throws InvalidPositionException if the provided position is null, smaller than 1, or greater than 10
     */
    static void isPositionValid(Position position) throws InvalidPositionException {

        if (position == null) throw new InvalidPositionException("Position is null");
        if (position.getX() < 1 || position.getX() >= 11) throw new InvalidPositionException("y is out-of-bounds");
        if (position.getY() < 1 || position.getY() >= 11) throw new InvalidPositionException("x is out-of-bounds");

    }

    /**
     * @return the ships on the board
     */
    HashMap<Placement, ShipInterface> getShips() {
        return this.ships;
    }

    public void placeShip(ShipInterface ship, Position position, boolean isVertical) throws InvalidPositionException, ShipOverlapException {

        Board.isPositionValid(position);
        Board.isPositionValid(new Position(
                position.getX() + (isVertical ? 0 : ship.getSize() - 1),
                position.getY() + (isVertical ? ship.getSize() - 1 : 0)
        ));

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            ShipInterface s = entry.getValue();
            Placement p = entry.getKey();

            for (int i = 0; i < s.getSize(); i++) {

               int x = p.isVertical() ? p.getPosition().getX() : p.getPosition().getX() + i;
               int y = p.isVertical() ? p.getPosition().getY() + i : p.getPosition().getY();

               for (int j = 0; j < ship.getSize(); j ++) {

                    int shipX = isVertical ? position.getX() : position.getX() + j;
                    int shipY = isVertical ? position.getY() + j : position.getY();

                    if (shipX == x && shipY == y) throw new ShipOverlapException("(" + x + ", " + y + ")");

               }

            }

        }

        this.ships.put(new Placement(position, isVertical), ship);

    }

    public void shoot(Position position) throws InvalidPositionException {

        Board.isPositionValid(position);

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            ShipInterface s = entry.getValue();
            Placement p = entry.getKey();

            for (int i = 0; i < s.getSize(); i++) {

                int x = p.isVertical() ? p.getPosition().getX() : p.getPosition().getX() + i;
                int y = p.isVertical() ? p.getPosition().getY() + i : p.getPosition().getY();

                if (x == position.getX() && y == position.getY()) {

                    s.shoot(i);
                    return;

                }

            }

        }

    }

    public ShipStatus getStatus(Position position) throws InvalidPositionException {

        Board.isPositionValid(position);

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            ShipInterface s = entry.getValue();
            Placement p = entry.getKey();

            for (int i = 0; i < s.getSize(); i++) {

                int x = p.isVertical() ? p.getPosition().getX() : p.getPosition().getX() + i;
                int y = p.isVertical() ? p.getPosition().getY() + i : p.getPosition().getY();

                if (x == position.getX() && y == position.getY()) {

                    return s.getStatus(i);

                }

            }

        }

        return ShipStatus.NONE;

    }

    public boolean allSunk() {

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            ShipInterface s = entry.getValue();

            if (!s.isSunk()) return false;

        }

        return true;

    }

    public BoardInterface clone() {

        Board board = new Board();

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            try {

                Placement p = entry.getKey();

                board.placeShip(entry.getValue(), p.getPosition(), p.isVertical());


            } catch (InvalidPositionException | ShipOverlapException e) {

                System.out.println("Error cloning board: " + e.toString());

            }

        }

       return board;

    }

    @Override
    public String toString() {

        String[][] board = new String[10][10];

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {

                board[i][j] = "_";

            }

        }

        for (Map.Entry<Placement, ShipInterface> entry: this.ships.entrySet()) {

            ShipInterface s = entry.getValue();
            Placement p = entry.getKey();

            for (int i = 0; i < s.getSize(); i++) {

                int x = p.isVertical() ? p.getPosition().getX() : p.getPosition().getX() + i;
                int y = p.isVertical() ? p.getPosition().getY() + i : p.getPosition().getY();

                try {

                    board[y - 1][x - 1] = s.getStatus(i).toString().substring(0, 1);

                } catch (InvalidPositionException e) {

                    System.out.println("Error generating board: " + e.toString());

                }

            }

        }

        StringBuilder sb = new StringBuilder();

        sb.append("   ");

        for (int i = 0; i < 10; i ++) {

            sb.append(" ").append(i + 1);

        }

        sb.append("\n   ");

        for (int i = 0; i < 10; i ++) {

            sb.append(" ").append("_");

        }

        sb.append("\n");

        for (int i = 0; i < board.length; i++) {

            String[] row = board[i];

            sb.append(i + 1).append(i == 9 ? "" : " ").append(" |");

            for (String cell: row) {

                sb.append(cell).append("|");

            }

            sb.append("\n");

        }

        return sb.toString();

    }

}
