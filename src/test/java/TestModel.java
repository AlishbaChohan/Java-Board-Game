import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import game.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestModel {
    BoardGameModel model = new BoardGameModel();

    public void exampleHorizontalState() {
        model.makeMove(new Position(4, 1), new Position(3, 1));
        model.makeMove(new Position(4, 3), new Position(3, 3));
        model.makeMove(new Position(0, 2), new Position(1, 2));
        model.makeMove(new Position(1, 2), new Position(2, 2));
        model.makeMove(new Position(2, 2), new Position(2, 3));


    }

    @Test
    public void isOnBoard() {
        assertTrue(BoardGameModel.isOnBoard(new Position(2, 1)));
        assertFalse(BoardGameModel.isOnBoard(new Position(5, 4)));
    }

    @Test()
    public void isEmpty() throws Exception {
        assertFalse(model.isEmpty(new Position(0, 0)));
        assertTrue(model.isEmpty(new Position(1, 1)));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> model.isEmpty(new Position(9, 10)));
    }

    @Test
    public void isKingMove() {
        assertTrue(BoardGameModel.isKingMove(new Position(0, 0), new Position(0, 1)));
        assertTrue(BoardGameModel.isKingMove(new Position(0, 0), new Position(1, 0)));
        assertFalse(BoardGameModel.isKingMove(new Position(0, 0), new Position(1, 1)));
        assertFalse(BoardGameModel.isKingMove(new Position(0, 0), new Position(2, 2)));

    }

    @Test
    public void isLegalMoveFrom() {
        assertTrue(model.isLegalToMoveFrom(new Position(0, 0)));
        assertFalse(model.isLegalToMoveFrom(new Position(3, 0)));
    }

    @Test
    public void isLegalMove() {
        assertTrue(model.isLegalMove(new Position(0, 0), new Position(1, 0)));
        assertFalse(model.isLegalMove(new Position(0, 0), new Position(1, 1)));
        assertFalse(model.isLegalMove(new Position(1, 0), new Position(1, 1)));
        assertFalse(model.isLegalMove(new Position(0, 0), new Position(0, 1)));

    }

    @Test
    public void makeMove() {
        model.numberOfMoves.set(0);
        assertEquals(model.numberOfMoves.get(),0); //no moves

        model.makeMove(new Position(0,0), new Position(1,0));// invalid move
        assertEquals(model.numberOfMoves.get(),1);

        model.makeMove(new Position(0,0), new Position(0,1));// invalid move, but a move as validity is checked by other method
        assertEquals(model.numberOfMoves.get(),2);

    }

    @Test
    public void checkWinning() {
        assertFalse(model.checkWinning(new Position(0,0)));
        assertTrue(model.checkWinning(new Position(1,0)));
    }

    @Test
    public void checkHorizontal() {
////      exampleHorizontalState();
//        model.makeMove(new Position(4,1),new Position(3,1));
//        model.makeMove(new Position(4,3),new Position(3,3));
//        model.makeMove(new Position(0,2),new Position(1,2));
//        model.makeMove(new Position(1,2),new Position(2,2));
//        model.makeMove(new Position(2,2),new Position(2,3));
//       int row = 3; // from the last move of the example state
//       int col = 2; // from the last move of the example state
//       assertTrue(model.checkHorizontal(3,2));

        // wanted to create a dummy state with above and call give the check horizontal the last move of the dummy state but could no implement
        Position tempPosition = new Position(0, 0);
        Position tempPosition2 = new Position(1, 0);

        assertFalse(model.checkHorizontal(tempPosition.row(), tempPosition.col()));
        assertTrue(model.checkHorizontal(tempPosition2.row(), tempPosition2.col())); // checks empty col
    }

    @Test
    public void checkVertical() {
        Position tempPosition = new Position(0, 0);
        Position tempPosition2 = new Position(1, 0);

        assertFalse(model.checkVertical(tempPosition.row(), tempPosition.col()));
        assertTrue(model.checkVertical(tempPosition2.row(), tempPosition2.col())); //checks empty col
    }

    @Test
    public void getNextPlayer() {
        State.Player tempCurrentPlayer =  State.Player.PLAYER_1;
        State.Player tempCurrentPlayer2 =  State.Player.PLAYER_2;
        assertNotEquals(model.getNextPlayer(), tempCurrentPlayer);
        assertEquals(model.getNextPlayer(), tempCurrentPlayer2);
    }

    @Test
    public void isGameOver() {
        assertFalse(model.isGameOver());
        model.gameOver.set(true);
        assertTrue(model.isGameOver());

    }

    @Test
    public void getStatus() {
        assertEquals(model.getStatus(), State.Status.IN_PROGRESS);
        model.gameOver.set(true);
        model.player.set(State.Player.PLAYER_2); // as getNextPlayer is called in make move before the checking of win
        assertEquals(model.getStatus(), State.Status.PLAYER_1_WINS);
    }

    @Test
    public void isWinner() {
        model.status.set(State.Status.PLAYER_1_WINS);
//        assertTrue(model.isWinner(model.player.get()));
// try later
    }


}
