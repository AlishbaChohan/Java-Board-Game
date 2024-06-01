package boardgame.model;

import game.TwoPhaseMoveState;
import javafx.beans.property.*;

/**
 * Represents the state and logic of the 5x4 board game.
 * The board contains alternating red and blue pieces in the squares on the first and last row.
 * All the other squares are empty.
 * The game can be played by moving a piece from initial position ({@code from}) to the final position({@code to}).
 * A piece moves on the board after satisfying certain conditions.
 */

public class BoardGameModel implements game.TwoPhaseMoveState<Position> {

    /**
     * The number of rows of the board.
     */
    public static final int BOARD_ROW = 5;

    /**
     * The number of columns of the board.
     */
    public static final int BOARD_COL = 4;
    private final ReadOnlyObjectWrapper<Square>[][] board;

    /**
     * The Count of number of moves per game.
     */
    public final ReadOnlyIntegerWrapper numberOfMoves;

    /**
     * The player currently playing the game.
     */
    public final ReadOnlyObjectWrapper<Player> player;

    /**
     * The game over state determiner.
     */
    public final ReadOnlyBooleanWrapper gameOver;

    /**
     * The current Status of game, for example: IN_Progress, DRAW etc.
     */
    public final ReadOnlyObjectWrapper<Status> status;
    private Square square;

    /**
     * Instantiate the initial state of the game and initial arrangement of pieces on the board.
     */
    public BoardGameModel() {
        board = new ReadOnlyObjectWrapper[BOARD_ROW][BOARD_COL];
        for (var i = 0; i < BOARD_ROW; i++) {
            for (var j = 0; j < BOARD_COL; j++) {
                board[i][j] = new ReadOnlyObjectWrapper<>(
                        switch (i) {
                            case 0 -> (j % 2 == 0) ? Square.BLUE : Square.RED;
                            case BOARD_ROW - 1 -> (j % 2 == 0) ? Square.RED : Square.BLUE;
                            default -> Square.NONE;
                        }
                );

            }
        }
        numberOfMoves = new ReadOnlyIntegerWrapper(0);
        player = new ReadOnlyObjectWrapper<>(Player.PLAYER_1);
        status = new ReadOnlyObjectWrapper<>(Status.IN_PROGRESS);
        status.set(Status.IN_PROGRESS);
        gameOver = new ReadOnlyBooleanWrapper();
        gameOver.set(false);
    }

    /**
     * Returns the property of the game over result.
     *
     * @return the value of gameOver variable
     */
    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }

    /**
     * Returns the current player property.
     *
     * @return the current player
     */
    public ReadOnlyObjectProperty<Player> playerProperty() {
        return player.getReadOnlyProperty();
    }

    /**
     * Returns the number of moves in each game.
     *
     * @return the number of moves per game
     */
    public ReadOnlyIntegerProperty numberOfMovesProperty() {
        return numberOfMoves.getReadOnlyProperty();
    }

    /**
     * Returns the square's property at the position specified.
     *
     * @param row the row index
     * @param col the column index
     * @return square's property at the specified position
     */
    public ReadOnlyObjectProperty<Square> squareProperty(int row, int col) {
        return board[row][col].getReadOnlyProperty();
    }

    /**
     * Returns the square at the position specified.
     *
     * @param p the position
     * @return square at the specified position
     */
    public Square getSquare(Position p) {
        return board[p.row()][p.col()].get();
    }

    /**
     * Set the square at the position specified.
     *
     * @param p      the position
     * @param square the square to be set
     */
    private void setSquare(Position p, Square square) {
        board[p.row()][p.col()].set(square);
    }

    /**
     * Determines if the square at the specified position is empty
     *
     * @param p the position
     * @return {@code true} if the position is empty,{@code false} otherwise
     */
    public boolean isEmpty(Position p) {
        return getSquare(p) == Square.NONE;
    }

    /**
     * Determines if the specified position is on the game board.
     *
     * @param p the position
     * @return {@code true} if the position is on the board,{@code false} otherwise
     */
    public static boolean isOnBoard(Position p) {
        return 0 <= p.row() && p.row() < BOARD_ROW && 0 <= p.col() && p.col() < BOARD_COL;
    }

    /**
     * Determines if the specified piece can move one step horizontally or vertically (Similar to King's move in chess)
     *
     * @param from the initial position
     * @param to   the final position
     * @return {@code true} if the piece can move one step horizontally or vertically,{@code false} otherwise
     */
    public static boolean isKingMove(Position from, Position to) {
        var dx = Math.abs(to.row() - from.row());
        var dy = Math.abs(to.col() - from.col());
        return dx + dy == 1;
    }

    /**
     * Determines if the specified position has a piece that can be moved.
     *
     * @param from the specified position
     * @return {@code true} if the,{@code false} otherwise
     */
    @Override
    public boolean isLegalToMoveFrom(Position from) {
        return isOnBoard(from) && !isEmpty(from);
    }

    /**
     * Determines if a specified position is legal.
     *
     * @param from initial position
     * @param to   final position
     * @return {@code true} if the piece can move one step horizontally or vertically,{@code false} otherwise
     */
    @Override
    public boolean isLegalMove(Position from, Position to) {
        return isLegalToMoveFrom(from) && isOnBoard(to) && isEmpty(to) && isKingMove(from, to);
    }

    /**
     * Makes the specified move.
     *
     * @param from initial position
     * @param to   final position
     */
    @Override
    public void makeMove(Position from, Position to) {
        setSquare(to, getSquare(from));
        setSquare(from, Square.NONE);
        numberOfMoves.set(numberOfMoves.get() + 1);
        player.set(getNextPlayer());
        checkWinning(to);
    }

    /**
     * Determines if there is a winning sequence at the specified position.
     *
     * @param p the specified position
     * @return {@code true} if there is a winning sequence,{@code false} otherwise
     */
    public boolean checkWinning(Position p) {
        if (checkHorizontal(p.row(), p.col()) || checkVertical(p.row(), p.col())) {
            gameOver.set(true);
            return true;
        }
        return false;
    }

    /**
     * Determines if there is a horizontal sequence of three adjacent pieces.
     *
     * @param row row index
     * @param col col index
     * @return {@code true} if there is a horizontal sequence,{@code false} otherwise
     */
    public boolean checkHorizontal(int row, int col) {
        int countAdjacent = 0;
        int actualCol = col;
        int expCol = 0;
        for (int i = 0; i < BOARD_COL; i++) {
            if (board[row][actualCol].get() == board[row][expCol + i].get()) {
                countAdjacent++;
                if (countAdjacent == 3) {
                    return true;
                }
            } else {
                countAdjacent = 0;
            }
        }
        return false;
    }

    /**
     * Determines if there is a vertical sequence of three adjacent pieces.
     *
     * @param row row index
     * @param col col index
     * @return {@code true} if there is a vertical sequence,{@code false} otherwise
     */
    public boolean checkVertical(int row, int col) {
        int countAdjacent = 0;
        int actualRow = row;
        int expRow = 0;
        for (int i = 0; i < BOARD_ROW; i++) {
            if (board[actualRow][col].get() == board[expRow + i][col].get()) {
                countAdjacent++;
                if (countAdjacent == 3) {
                    return true;
                }
            } else {
                countAdjacent = 0;
            }
        }
        return false;
    }

    /**
     * {@return the player who moves next}
     */
    @Override
    public Player getNextPlayer() {
        return player.get().opponent();
    }

    /**
     * {@return whether the game is over}
     */
    @Override
    public boolean isGameOver() {
        return gameOver.get();
    }

    /**
     * {@return the status of the game}
     */
    @Override
    public Status getStatus() {
        if (!isGameOver()) {
            return Status.IN_PROGRESS;
        }
        return player.get() == Player.PLAYER_2 ? Status.PLAYER_1_WINS : Status.PLAYER_2_WINS;
    }

    /**
     * {@return whether the player specified has won the game}
     *
     * @param player the player to be tested for the win
     */
    @Override
    public boolean isWinner(Player player) {
        return TwoPhaseMoveState.super.isWinner(player);
    }

    /**
     * {@return a string representation of the game state}
     */
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_ROW; i++) {
            for (var j = 0; j < BOARD_COL; j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

}
