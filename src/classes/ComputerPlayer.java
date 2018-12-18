package classes;

import enums.ShotStatus;
import exceptions.InvalidPositionException;
import exceptions.PauseException;
import exceptions.ShipOverlapException;
import interfaces.BoardInterface;
import interfaces.PlayerInterface;
import interfaces.ShipInterface;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

public class ComputerPlayer implements PlayerInterface {

    private ShotBoard shots = new ShotBoard();
    private ProbabilityDensityGrid densityGrid = new ProbabilityDensityGrid();
    private Deque<Position> targets = new ArrayDeque<>();
    private String name;

    public ComputerPlayer(String name) {
        this(name, new ShotBoard());
    }

    ComputerPlayer(String name, ShotBoard shots) {

        this.name = name;
        this.shots = shots;

        this.densityGrid.update();

    }

    public Position chooseShot() {

        if (this.targets.isEmpty()) {

            return this.densityGrid.getMaxPosition();

        } else {

            return this.targets.pop();

        }

    }

    public Placement choosePlacement(ShipInterface ship, BoardInterface board) throws PauseException {

        Random r = new Random();

        while (true) {

            try {

                Position p = new Position(r.nextInt(10) + 1, r.nextInt(10) + 1);
                boolean isVertical = r.nextBoolean();

                board.placeShip(ship, p, isVertical);

                return new Placement(p, isVertical);

            } catch (InvalidPositionException | ShipOverlapException e) {}

        }

    }

    public void shotResult(Position position, ShotStatus status) {

        this.shots.addShot(position, status);

        if (status == ShotStatus.HIT) {

            int[] x = new int[]{0, 1, 0, -1}, y = new int[]{-1, 0, 1, 0};

            for (int i = 0; i < 4; i ++) {

                try {

                    Position newTarget = new Position(position.getX() + x[i], position.getY() + y[i]);

                    if (!this.shots.getShots().containsKey(newTarget) && !this.targets.contains(newTarget)) {

                        this.targets.push(newTarget);

                    }

                } catch (InvalidPositionException e) {}

            }

        }

        this.densityGrid.update();

    }

    public void opponentShot(Position position) {}

    @Override
    public String toString() {
        return this.name;
    }

    ShotBoard getShots() {
        return this.shots;
    }

    private class ProbabilityDensityGrid {

        private int[][] densityGrid = new int[10][10];
        private int parity = 2;

        /**
         * Wipes and refills the grid
         */
        void update() {

            for (int j = 0; j < 10; j++) {

                for (int i = 0; i < 10; i++) {

                    this.densityGrid[j][i] = 0;

                }

            }

            int[] ships = new int[]{2, 3, 3, 4, 5};

            for (int ship: ships) {

                this.addShip(ship);

            }
        }

        /**
         * @return the position with the greatest probability of containing a ship
         */
        Position getMaxPosition() {

            Position p = null;
            int max = 0;

            for (int i = 0; i < Math.floor(100 / parity); i++) {

                int y = (i * parity) / 10, x = ((i * parity) + y) % 10;

                if (this.densityGrid[y][x] > max) {

                    try {

                        Position temp = new Position(x + 1, y + 1);

                        if (!shots.getShots().containsKey(temp)) {

                            max = this.densityGrid[y][x];
                            p = temp;

                        }

                    } catch (InvalidPositionException e) {}

                }

            }

            return p;

        }

        /**
         * Attempts to place a ship in every possible cell and orientation. For each successful placement, increase the
         * probability held within that cell (of a ship being present)
         *
         * @param shipSize The size of the ship to attempt to add
         */
         private void addShip(int shipSize) {

            for (int y = 1; y < 11; y++) {

                for (int x = 1; x < 11; x++) {

                    for (int k = 0; k < 2; k++) {

                        boolean isVertical = k == 0;

                        try {

                            for (int offset = 0; offset < shipSize; offset++) {

                                Position p = new Position(isVertical ? x : x + offset, isVertical ? y + offset: y);

                                Board.isPositionValid(p);

                                if (shots.getShots().containsKey(p) && shots.getShots().get(p) == ShotStatus.MISS) {

                                    throw new ShipOverlapException("Already in shot board");

                                }

                            }

                        } catch (InvalidPositionException | ShipOverlapException e) {

                            continue;

                        }

                        for (int offset = 0; offset < shipSize; offset++) {

                            this.densityGrid[(isVertical ? y + offset: y) - 1][(isVertical ? x : x + offset) - 1]++;

                        }

                    }

                }

            }

        }

        @Override
        public String toString() {

            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < 10; j++) {

                for (int i = 0; i < 10; i++) {

                    sb.append(this.densityGrid[j][i]).append(" ");

                }

                sb.append("\n");

            }

            return sb.toString();

        }

    }

}
