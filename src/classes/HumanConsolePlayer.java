package classes;

import enums.ShotStatus;
import exceptions.InvalidPositionException;
import exceptions.PauseException;
import interfaces.BoardInterface;
import interfaces.PlayerInterface;
import interfaces.ShipInterface;

import java.util.Scanner;

public class HumanConsolePlayer implements PlayerInterface {

    private String name;
    private ShotBoard shots;

    HumanConsolePlayer(String name) {
        this(name, new ShotBoard());
    }

    HumanConsolePlayer(String name, ShotBoard shots) {

        this.name = name;
        this.shots = shots;

    }

    ShotBoard getShots() {
        return this.shots;
    }

    public Placement choosePlacement(ShipInterface ship, BoardInterface board) throws PauseException {

        System.out.println(board.toString());
        System.out.println("Please enter the co-ordinates of your ship (" + ship.getSize() + "): ");

        Position p = this.getCoordinateInput();
        boolean isVertical = this.getIsVertical();

        return new Placement(p, isVertical);

    }

    /**
     * @return whether the player wants the ship to be vertical or not
     * @throws PauseException if 'pause' is entered.
     */
    private boolean getIsVertical() throws PauseException {

        Scanner keyboard = new Scanner(System.in);

        while (true) {

            System.out.print("Is the ship vertical? (Y/N): ");
            String input = keyboard.next().toLowerCase();

            if (input.equals("pause")) throw new PauseException();

            if (!input.equals("y") && !input.equals("n")) {

                System.out.println("Invalid input. Try again");

            } else {

                return input.equals("y");

            }

        }

    }

    /**
     * @return a position of the players's choosing
     * @throws PauseException if 'pause' is entered
     */
    private Position getCoordinateInput() throws PauseException {

        Scanner keyboard = new Scanner(System.in);
        Integer[] coordinates = new Integer[2];

        while(true) {

            for (int i = 0; i < coordinates.length; i++) {

                while(true) {

                    System.out.print((i == 0 ? "x" : "y") + ": ");

                    String input = keyboard.nextLine();

                    if (input.toLowerCase().equals("pause")) throw new PauseException();

                    try {

                        coordinates[i] = Integer.valueOf(input);
                        break;

                    } catch (NumberFormatException e) {

                        System.out.println("Invalid co-ordinate. Try again.");

                    }

                }

            }

            try {

                return new Position(coordinates[0], coordinates[1]);

            } catch (InvalidPositionException e) {

                System.out.println("\nInvalid position. Try again.");

            }

        }

    }

    public Position chooseShot() throws PauseException {

        System.out.println(this.shots.toString());
        System.out.println("Please enter the co-ordinates of your shot:");

        while (true) {

            Position p = this.getCoordinateInput();

            if (!this.shots.getShots().containsKey(p)) {

                System.out.println("\n*******************************************");

                return p;

            } else {

                System.out.println("You have already fired at this location. Please choose another.");

            }

        }

    }

    public void shotResult(Position position, ShotStatus status) {

        this.shots.addShot(position, status);

        System.out.println("\nShot result:\n");
        System.out.println(this.shots.toString());

    }

    public void opponentShot(Position position) {

        System.out.println("Your opponent fired at " + position.toString());

    }

    @Override
    public String toString() {
        return this.name;
    }

}
