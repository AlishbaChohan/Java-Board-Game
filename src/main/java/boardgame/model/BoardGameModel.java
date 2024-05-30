package boardgame.model;

import game.TwoPhaseMoveState;
import javafx.beans.property.*;


public class BoardGameModel implements TwoPhaseMoveState<Position> {

    public static final int BOARD_SIZE = 5;

    private final ReadOnlyObjectWrapper<Square>[][] board;


    private final ReadOnlyIntegerWrapper numberOfMoves;

    private final ReadOnlyObjectWrapper<Player> player;
    private final ReadOnlyBooleanWrapper gameOver;
//    private

    // private Player player;

    // private Player currentPlayer;
    private final ReadOnlyObjectWrapper<Status> status;

    private Square square;

    public BoardGameModel() {
        board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE - 1];
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < (BOARD_SIZE - 1); j++) {
                board[i][j] = new ReadOnlyObjectWrapper<>(
                        switch (i) {
                            case 0 -> (j % 2 == 0) ? Square.BLUE : Square.RED;
                            case BOARD_SIZE - 1 -> (j % 2 == 0) ? Square.BLUE : Square.RED;
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
        //player.set(Player.PLAYER_1);
///maybe here

    }

    public ReadOnlyBooleanProperty gameOverProperty() {
        return gameOver.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Player> playerProperty() {
        return player.getReadOnlyProperty();
    }


    public ReadOnlyIntegerProperty numberOfMovesProperty() {
        return numberOfMoves.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Square> squareProperty(int row, int col) {
        return board[row][col].getReadOnlyProperty();
    }

    public Square getSquare(Position p) {
        return board[p.row()][p.col()].get();
    }

    private void setSquare(Position p, Square square) {
        board[p.row()][p.col()].set(square);
    }

    public boolean isEmpty(Position p) {
        return getSquare(p) == Square.NONE;
    }

    public static boolean isOnBoard(Position p) {
        return 0 <= p.row() && p.row() < BOARD_SIZE && 0 <= p.col() && p.col() < BOARD_SIZE;
    }


    public static boolean isKingMove(Position from, Position to) {
        var dx = Math.abs(to.row() - from.row());
        var dy = Math.abs(to.col() - from.col());
        return dx + dy == 1;
    }


    // Thes ones below are also working fine

    @Override
    public boolean isLegalToMoveFrom(Position from) {
        return isOnBoard(from) && !isEmpty(from);
    }

    @Override
    public boolean isLegalMove(Position from, Position to) {
        return isLegalToMoveFrom(from) && isOnBoard(to) && isEmpty(to) && isKingMove(from, to);
    }

    @Override
    public void makeMove(Position from, Position to) {
        setSquare(to, getSquare(from));
        setSquare(from, Square.NONE);
        numberOfMoves.set(numberOfMoves.get() + 1);
        player.set(getNextPlayer());
        checkWinning(to);


    }

    public void checkWinning(Position p) {
        if (checkHorizontal(p.row(), p.col()) || checkVertical(p.row(), p.col())) {
            gameOver.set(true);
        }
    }


    public boolean checkHorizontal(int row, int col){
        int countAdjacent = 0;
        int actualCol = col;
        int expCol = 0;
        for(int i = 0; i < (BOARD_SIZE-1); i++){
            if(board[row][actualCol].get() == board[row][expCol + i].get()) {
                countAdjacent++;
            }
        }
        if (countAdjacent == 3){
            return true;
        }

        return false;

    }

    public boolean checkVertical(int row, int col){
        int countAdjacent = 0;
        int actualRow = row;
        int expRow = 0;
        for(int i = 0; i < BOARD_SIZE; i++){
            if(board[actualRow][col].get() == board[expRow + i][col].get()) {
                countAdjacent++;
            }
        }
        if (countAdjacent == 3){
            return true;
        }

        return false;

    }

    

//    public boolean checkHorizontal(int row, int col){
//        int countAdjacent = 0; // Start with 1 as we include the starting position itself
//        int actualCol = col;
//        int expCol = 0;
//
//        // Check to the right of the starting position
//        for (int i = 0; i < BOARD_SIZE - 1; i++) {
//            if (board[row][actualCol].get() == board[row][expCol + i].get()) {
//                countAdjacent++;
//                if (countAdjacent == 2) {
//                    return true;
//                }
//            } else {
//                countAdjacent = 0; // Reset the count if squares are not matching
//            }
//        }return false;
//    }



    // I do not understand how things are going with the functions below:

    @Override
    public Player getNextPlayer() {
        return player.get().opponent();
    }

    @Override
    public boolean isGameOver() {
        return gameOver.get();
    }

    @Override
    public Status getStatus() {
        if (!isGameOver()) {
            return Status.IN_PROGRESS;
        }
//        return null;
        return player.get() == Player.PLAYER_2 ? Status.PLAYER_1_WINS : Status.PLAYER_2_WINS;
        // Logger.info("player");
    }

    @Override
    public boolean isWinner(Player player) {
        return TwoPhaseMoveState.super.isWinner(player);
    }


}
