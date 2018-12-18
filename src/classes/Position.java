package classes;

import exceptions.InvalidPositionException;

import java.util.Objects;

public class Position {

    // Except I didn't consolidate it in one place; I did so in both.

    private int x, y;
    
    public Position(int x, int y) throws InvalidPositionException {
        setX(x);
        setY(y);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    /**
     * @param x the x position of the location. 1 is on the left, 10 is on the right
     * @throws InvalidPositionException if the parameter is less than 0 or more than 10
     */
    private void setX(int x) throws InvalidPositionException {

        if (x < 1 || x > 10) {

            throw new InvalidPositionException(Integer.toString(x));

        }

        this.x = x;

    }

    /**
     * @param y the y position of the location. 1 is at the top, 10 is at the bottom
     * @throws InvalidPositionException if the parameter is less than 1 or more than 10
     */
    private void setY(int y) throws InvalidPositionException {

        if (y < 1 || y > 10) {

            throw new InvalidPositionException(Integer.toString(y));

        }

        this.y = y;

    }
    
    @Override
    public String toString() {

        return x + "," + y;

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y);

    }

}
