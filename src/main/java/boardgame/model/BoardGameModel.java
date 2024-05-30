package boardgame.model;

import game.TwoPhaseMoveState;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;



public class BoardGameModel implements TwoPhaseMoveState<Position> {

    public static final int BOARD_SIZE = 5;

    private final ReadOnlyObjectWrapper<Square>[][] board;


    private final ReadOnlyIntegerWrapper numberOfMoves;

    private final ReadOnlyObjectWrapper<Player> player;
//    private

//    private Player player;

   // private Player currentPlayer;

    public BoardGameModel() {
        board = new ReadOnlyObjectWrapper[BOARD_SIZE][BOARD_SIZE-1];
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
//        player = new ReadOnlyObjectWrapper<>(Player.PLAYER_1);

        player = new ReadOnlyObjectWrapper<>(Player.PLAYER_1);


///maybe here

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
        return dx + dy == 1 ;
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
        numberOfMoves.set(numberOfMoves.get() +  1);
        player.set(getNextPlayer());

    }
    public void checkWinning(Position p){
        if(checkHorizontal(p.row(), p.col())){

        };
    }


    public boolean checkHorizontal(int row, int col){
        int countAdjacent = 0;
        int actualCol = col;
        int expCol = 0;
        for(int i = 0; i < (BOARD_SIZE-1); i++){
            if(board[row][actualCol] == board[row][expCol +i]) {
                countAdjacent++;
            }
        }
        if (countAdjacent == 3){
            return true;
        }

        return false;

    }







    // I do not understand how things are going with the functions below:

    @Override
    public Player getNextPlayer() {
        return player.get().opponent();
    }

    @Override
    public boolean isGameOver() {

            return false;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public boolean isWinner(Player player) {
        return TwoPhaseMoveState.super.isWinner(player);
    }


}
