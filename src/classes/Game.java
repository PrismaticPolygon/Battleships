package classes;

import enums.ShipStatus;
import enums.ShotStatus;
import exceptions.InvalidPositionException;
import exceptions.PauseException;
import exceptions.ShipOverlapException;
import interfaces.BoardInterface;
import interfaces.GameInterface;
import interfaces.PlayerInterface;
import interfaces.ShipInterface;

import java.io.*;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

public class Game implements GameInterface {

    private PlayerInterface player1, player2, currentPlayer, opponent;
    private int turnCount = 0;
    private FileManager fileManager;
    private BoardInterface board1, board2;

    private Game(PlayerInterface player1, PlayerInterface player2) {

        this.player1 = this.currentPlayer = player1;
        this.player2 = this.opponent = player2;

        this.board1 = new Board();
        this.board2 = new Board();

        this.fileManager = new FileManager();

    }

    public PlayerInterface play() {

        try {

            placeShips();

            while(true) {

                currentPlayer = turnCount % 2 == 0 ? player1 : player2;
                opponent = turnCount % 2 == 0 ? player2 : player1;
                BoardInterface opponentBoard = turnCount % 2 == 0 ? board2 : board1;

                PlayerInterface winner = takeTurn(currentPlayer, opponent, opponentBoard);

                if (winner != null) return winner;

                turnCount++;

            }

        } catch (PauseException e) {

            return null;

        } catch (InvalidPositionException e) {

            if (e.getMessage().equals(player1.toString())) return player2;
            else return player1;

        }

    }

    public void saveGame(String filename) throws IOException {

        fileManager.saveGame(filename);

    }

    public void loadGame(String filename) throws IOException {

        fileManager.loadGame(filename);

    }

    private void placeShips() throws PauseException, InvalidPositionException {

        Board board = (Board) (currentPlayer.equals(this.player1) ? board1 : board2);
        int j = currentPlayer.equals(this.player1) ? 0 : 1;
        int[] ships = Arrays.copyOfRange(new int[]{2, 3, 3, 4, 5}, board.getShips().size(), 5);

        for (int i : ships) {

            Ship ship;

            for (; j < 2; j++) {

                ship = new Ship(i);

                try {

                    if (currentPlayer instanceof HumanConsolePlayer) {

                        System.out.println(currentPlayer + "'s turn\n");

                    }

                    Placement p = currentPlayer.choosePlacement(ship, board.clone());

                    board.placeShip(ship, p.getPosition(), p.isVertical());

                } catch (InvalidPositionException | ShipOverlapException e) {

                    System.out.println("Error placing ship!");

                    throw new InvalidPositionException(currentPlayer.toString());

                }

                currentPlayer = currentPlayer.equals(this.player1) ? player2 : player1;
                opponent = currentPlayer.equals(this.player1) ? player2 : player1;
                board = (Board) (currentPlayer.equals(this.player1) ? board1 : board2);

                if (currentPlayer instanceof HumanConsolePlayer) {

                    System.out.println("\n*******************************************\n");

                }

            }

            j = 0;

            if (currentPlayer instanceof HumanConsolePlayer && i == 5) {

                System.out.println(currentPlayer + "'s board\n");

                System.out.print(board.toString());

                System.out.println("\n*******************************************\n");

            }

        }


    }

    private PlayerInterface takeTurn(PlayerInterface player,
                                    PlayerInterface opponent,
                                    BoardInterface opponentBoard) throws PauseException, InvalidPositionException {

        System.out.println(player + "'s turn\n");

        //95 on average -> 48 turns. Not too bad.

        try {

            Position p = player.chooseShot();
            opponentBoard.shoot(p);

            ShotStatus s;

            if (opponentBoard.getStatus(p) == ShipStatus.HIT) s = ShotStatus.HIT;
            else if (opponentBoard.getStatus(p) == ShipStatus.SUNK) s = ShotStatus.SUNK;
            else s = ShotStatus.MISS;

            player.shotResult(p, s);
            opponent.opponentShot(p);

            if (player instanceof ComputerPlayer && !(opponent instanceof ComputerPlayer)) {

                System.out.println("\n" + opponent + "'s board: \n");

                System.out.println(opponentBoard.toString() + "\n");

            }

            // Add turn counter for each

            if (opponentBoard.allSunk()) return player;

        } catch (InvalidPositionException e) {

            throw new InvalidPositionException(player.toString());

        }

        System.out.println("*******************************************\n");

        return null;

    }

    public static void main(String[] args) {

        PlayerInterface player1 = new HumanConsolePlayer("Dave"), player2 = new ComputerPlayer("HAL 9000");
        Game game = null;

        Game.printMessage("Welcome to Battleships!");

        while (true) {

            switch(Game.getChoice()) {

                //Start new game
                case 1:

                    game = new Game(player1, player2);

                    Game.printMessage("Starting new game");
                    Game.playGame(game);

                    break;

                //Load game
                case 2:

                    game = Game.loadGame(new Game(player1, player2));

                    System.out.println("\n*******************************************\n");

                    if (game != null) Game.playGame(game);

                    break;

                //Continue game
                case 3:

                    Game.printMessage("Continuing game");

                    if (game != null) Game.playGame(game);
                    else System.out.println("No game to continue");
                    System.out.println("\n*******************************************\n");

                    break;

                //Save game
                case 4:

                    Game.saveGame(game);
                    System.out.println("\n*******************************************\n");

                    break;

                //Choose players
                case 5:

                    Game.printMessage("Choose players");
                    player1 = Game.choosePlayer(1);

                    System.out.println("\n*******************************************\n");

                    player2 = Game.choosePlayer(2);

                    System.out.println("\n*******************************************\n");

                    System.out.println("The next game will be: " + player1.toString() + " vs. " + player2.toString());

                    System.out.println("\n*******************************************\n");

                    break;

                case 6:

                    Game.printMessage("Thanks for playing!");
                    System.exit(0);

                    break;

                default:

                    System.out.println("Please enter a valid choice.");
                    System.out.println("\n*******************************************\n");
                    break;
            }

        }

    }

    private static void playGame(Game game) {

        PlayerInterface winner = game.play();

        String message = winner != null ? winner.toString() + " won!" : "Game paused";

        Game.printMessage(message);

    }

    private static void printMessage(String message) {

        StringBuilder sb = new StringBuilder();

        sb.append("\n*******************************************\n");
        sb.append("*****");

        for (int i = 0; i < (43 - message.length() - 10) / 2; i++) {

            sb.append(" ");

        }

        sb.append(message);

        for (int i = 0; i < (43 - message.length() - 10) / 2; i++) {

            sb.append(" ");

        }

        sb.append("*****\n");
        sb.append("*******************************************\n");

        System.out.println(sb.toString());

    }

    private static int getChoice() {

        Scanner input = new Scanner(System.in);
        int choice;

        System.out.print("1. New game\n" +
                "2. Load game\n" +
                "3. Continue game\n" +
                "4. Save game\n" +
                "5. Choose players\n" +
                "6. Exit game\n\n" +
                "Please select a choice: ");

        try {

            choice = input.nextInt();

        } catch (InputMismatchException e) {

            choice = 7;

        } finally {

            input.nextLine();

        }

        return choice;

    }

    private static Game loadGame(Game game) {

        Scanner input = new Scanner(System.in);

        Game.printMessage("Loading game");
        System.out.print("Please enter a filename: ");

        String filename = input.nextLine();

        try {

            game.loadGame(filename);
            System.out.println("Game loaded!");

            return game;

        } catch (IOException e) {

            System.out.println("Error loading game: " + e.toString());
            return null;

        }

    }

    private static void saveGame(Game game) {

        Game.printMessage("Saving game");
        Scanner input = new Scanner(System.in);
        String filename;

        if (game != null) {

            System.out.print("Please enter a filename: ");
            filename = input.nextLine();

            try {

                game.saveGame(filename);
                System.out.println("Game saved!");

            } catch (IOException e) {

                System.out.println("Error saving game: " + e.toString());

            }

        } else {

            System.out.println("No game to save");

        }

    }

    private static PlayerInterface choosePlayer(int playerNumber) {

        Scanner input = new Scanner(System.in);
        String playerType = "";

        while(!playerType.equals("H") && !playerType.equals("C")) {

            System.out.print("Is player " + playerNumber + " a human (H) or computer (C): ");
            playerType = input.nextLine().toUpperCase();

        }

        System.out.print("Please enter a name for player " + playerNumber + ": ");
        String name = input.nextLine();

        return playerType.equals("H") ? new HumanConsolePlayer(name) : new ComputerPlayer(name);

    }

    private class FileManager {

        private void loadGame(String filename) throws IOException {

            File dir = new File(filename);
            File[] files = dir.listFiles();

            if (files == null || files.length == 0) throw new IOException("File not found: " + filename);

            board1 = loadBoard(filename + "/board1.txt");
            board2 = loadBoard(filename + "/board2.txt");
            player1 = loadPlayer(filename + "/player1.txt");
            player2 = loadPlayer(filename + "/player2.txt");
            loadMeta(filename + "/meta.txt");

            System.out.println("Player1: " + player1);
            System.out.println("Player2: " + player2);
            System.out.println("Current player: " + currentPlayer);
            System.out.println("Opponent: " + opponent);

        }

        private void saveMeta(String filename) throws IOException {

            BufferedWriter bf = new BufferedWriter(new FileWriter(filename));

            if (currentPlayer.equals(player1)) bf.write("player1");
            else bf.write("player2");

            bf.newLine();
            bf.write(Integer.toString(turnCount));

            bf.close();

        }

        private void loadMeta(String filename) throws IOException {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();
            int count = 0;

            while (line != null) {

                if (count == 0) {

                    if (line.equals("player1")) {

                        currentPlayer = player1;
                        opponent = player2;

                    } else {

                        currentPlayer = player2;
                        opponent = player1;

                    }

                } else {

                    turnCount = Integer.valueOf(line);

                }

                line = br.readLine();
                count++;

            }

        }

        private void saveGame(String filename) throws IOException {

            File directory = new File(filename);

            if (!directory.exists()) directory.mkdir();

            this.saveBoard(filename + "/board1.txt", board1);
            this.saveBoard(filename + "/board2.txt", board2);
            this.savePlayer(filename + "/player1.txt", player1);
            this.savePlayer(filename + "/player2.txt", player2);
            this.saveMeta(filename + "/meta.txt");

        }

        private void saveBoard(String filename, BoardInterface board) throws IOException {

            BufferedWriter bf = new BufferedWriter(new FileWriter(filename));

            for (Map.Entry<Placement, ShipInterface> entry: ((Board) board).getShips().entrySet()) {

                bf.write(entry.getKey().toString() + "," + entry.getValue().toString());

                bf.newLine();

            }

            bf.close();

        }

        private void savePlayer(String filename, PlayerInterface player) throws IOException {

            BufferedWriter bf = new BufferedWriter(new FileWriter(filename));

            bf.write(player.toString());
            bf.newLine();

            bf.write(player instanceof HumanConsolePlayer ? "HumanConsolePlayer" : "ComputerPlayer");
            bf.newLine();

            ShotBoard shots;

            if (player instanceof HumanConsolePlayer) {

                shots = ((HumanConsolePlayer) player).getShots();

            } else {

                shots = ((ComputerPlayer) player).getShots();

            }

            for (Map.Entry<Position, ShotStatus> entry: shots.getShots().entrySet()) {

                bf.write(entry.getKey().toString() + "," + entry.getValue().toString());
                bf.newLine();

            }

            bf.close();

        }

        private Board loadBoard(String filename) throws IOException {

            Board board = new Board();

            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine();

            while (line != null) {

                String components[] = line.split(",");

                try {

                    Position p = new Position(Integer.valueOf(components[0]), Integer.valueOf(components[1]));
                    boolean isVertical = Boolean.valueOf(components[2]);
                    ShipStatus[] status = new ShipStatus[components.length - 3];

                    for (int i = 3; i < components.length; i++) {

                        status[i - 3] = ShipStatus.valueOf(components[i]);

                    }

                    ShipInterface s = new Ship(status);

                    board.placeShip(s, p, isVertical);

                } catch (InvalidPositionException | ShipOverlapException e) {

                    throw new IOException("Data corrupted: " + e.toString());

                }

                line = br.readLine();

            }

            return board;

        }

        private PlayerInterface loadPlayer(String filename) throws IOException {

            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line = br.readLine(), name = null, type = null;
            ShotBoard shots = new ShotBoard();
            int count = 0;

            while (line != null) {

                if (count == 0) {

                    name = line;

                } else if (count == 1) {

                    type = line;

                } else {

                    String components[] = line.split(",");

                    String positions[] = Arrays.copyOfRange(components, 0, 2),
                            statuses[] = Arrays.copyOfRange(components, 2, components.length);

                    try {

                        Position p = new Position(Integer.valueOf(positions[0]), Integer.valueOf(positions[1]));

                        ShotStatus status = ShotStatus.valueOf(statuses[0]);

                        shots.addShot(p, status);


                    } catch (InvalidPositionException e) {

                        throw new IOException("Data corrupted: " + e.toString());

                    }

                }

                count++;
                line = br.readLine();

            }

            if (type.equals("HumanConsolePlayer")) return new HumanConsolePlayer(name, shots);
            else return new ComputerPlayer(name, shots);

        }
    }

}
