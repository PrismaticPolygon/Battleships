package classes;

import java.util.Objects;

public class Placement {

    private Position position;
    private boolean isVertical;
    
    Placement(Position position, boolean isVertical) {

        this.position = position;
        this.isVertical = isVertical;

    }
    
    public Position getPosition() {
        return position;
    }
    
    boolean isVertical() {
        return isVertical;
    }

    @Override
    public String toString() {

        return this.position.toString() + "," + this.isVertical;

    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Placement placement = (Placement) o;

        return isVertical == placement.isVertical && Objects.equals(position, placement.position);
    }

    @Override
    public int hashCode() {

        return Objects.hash(position, isVertical);

    }

}
