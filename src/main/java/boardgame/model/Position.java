package boardgame.model;

/**
 * Represents the 2D position of the board squares.
 * @param row  the row of the position.
 * @param col  the column of the position.
 */
public record Position(int row, int col) {

    @Override
    public String toString() {
        return String.format("(%d,%d)", row, col);
    }

}
