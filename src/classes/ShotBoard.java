package classes;

import enums.ShotStatus;

import java.util.HashMap;
import java.util.Map;

public class ShotBoard {

    private HashMap<Position, ShotStatus> shots = new HashMap<>();

    void addShot(Position position, ShotStatus status) {
        this.shots.put(position, status);
    }

    HashMap<Position, ShotStatus> getShots() {
        return this.shots;
    }

    @Override
    public String toString() {

        String[][] board = new String[10][10];

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {

                board[i][j] = "_";

            }

        }

        for (Map.Entry<Position, ShotStatus> entry: this.shots.entrySet()) {

            board[entry.getKey().getY() - 1][entry.getKey().getX() - 1] = entry.getValue().toString().substring(0, 1);

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
