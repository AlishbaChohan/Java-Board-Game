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
       // currentPlayer = Player.PLAYER_1;
///maybe here

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




//    // Created these functions below to check the sequence but it ias causing errors
//    public boolean checkHorizontalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (getNextPlayer() == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
////        // Ensure the sequence does not go out of bounds horizontally
////        if (col + 2 >= BOARD_SIZE) {
////            return false;
////        }
//
//        // Check for a horizontal sequence
//        return board[row][col].get() == playerSquare &&
//                board[row][col + 1].get() == playerSquare &&
//                board[row][col + 2].get() == playerSquare;
//    }
//
//    public boolean checkReverseHorizontalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
////        // Ensure the sequence does not go out of bounds horizontally
////        if (col - 2 < 0) {
////            return false;
////        }
//
//        // Check for a reverse horizontal sequence
//        return board[row][col].get() == playerSquare &&
//                board[row][col - 1].get() == playerSquare &&
//                board[row][col - 2].get() == playerSquare;
//    }
//
//
//    // Check for a vertical sequence
//    public boolean checkVerticalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
//
////        if (row + 2 >= BOARD_SIZE) {
////            return false;
////        }
//
//
//        return board[row][col].get() == playerSquare &&
//                board[row + 1][col].get() == playerSquare &&
//                board[row + 2][col].get() == playerSquare;
//    }
//
//    // Check for a reverse vertical sequence
//    public boolean checkReverseVerticalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
////
////        if (row - 2 < 0) {
////            return false;
////        }
//
//        return board[row][col].get() == playerSquare &&
//                board[row - 1][col].get() == playerSquare &&
//                board[row - 2][col].get() == playerSquare;
//    }
//
//    // Check for a diagonal sequence
//    public boolean checkDiagonalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
//
////        if (row + 2 >= BOARD_SIZE || col + 2 >= BOARD_SIZE) {
////            return false;
////        }
//
//
//        return board[row][col].get() == playerSquare &&
//                board[row + 1][col + 1].get() == playerSquare &&
//                board[row + 2][col + 2].get() == playerSquare;
//    }
//
//    // Check for a reverse diagonal sequence
//    public boolean checkReverseDiagonalSeq(Position p) {
//        int row = p.row();
//        int col = p.col();
//
//        Square playerSquare = (currentPlayer == Player.PLAYER_1) ? Square.BLUE : Square.RED;
//
////        if (row - 2 < 0 || col + 2 >= BOARD_SIZE) {
////            return false;
////        }
//
//        return board[row][col].get() == playerSquare &&
//                board[row - 1][col + 1].get() == playerSquare &&
//                board[row - 2][col + 2].get() == playerSquare;
//    }
//
//    public boolean checkSeq(Position to) {
//        return checkHorizontalSeq(to) || checkReverseHorizontalSeq(to)
//                || checkVerticalSeq(to) || checkReverseVerticalSeq(to)
//                || checkDiagonalSeq(to) || checkReverseDiagonalSeq(to);
//
//    }







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

//        if(checkSeq(to)){
//            isWinner(currentPlayer);
//            System.out.println(currentPlayer + "won");
//        }
//        getNextPlayer();



    }



    //Understand and use for movement
    @Override
    public String toString() {
        var sb = new StringBuilder();
        for (var i = 0; i < BOARD_SIZE; i++) {
            for (var j = 0; j < (BOARD_SIZE - 1); j++) {
                sb.append(board[i][j].get().ordinal()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        var model = new BoardGameModel();
        System.out.println(model);
    }



    // I do not understand how things are going with the functions below:

    @Override
    public Player getNextPlayer() {
//        currentPlayer = (currentPlayer == Player.PLAYER_1) ? Player.PLAYER_2 : Player.PLAYER_1;
//        return currentPlayer;
        return null;
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
