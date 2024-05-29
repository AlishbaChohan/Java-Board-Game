package boardgame.util;

import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;

public class BoardGameMoveSelector{


    public enum Phase {
        SELECT_FROM,
        SELECT_TO,
        READY_TO_MOVE

    }

    private final BoardGameModel model;
    private final ReadOnlyObjectWrapper<Phase> phase;
    private boolean invalidSelection;
    private Position from;
    private Position to;

    public BoardGameMoveSelector(BoardGameModel model) {
        this.model = model;
        phase = new ReadOnlyObjectWrapper<>(Phase.SELECT_FROM);
        invalidSelection = false;
    }

    public Phase getPhase() {
        return phase.get();
    }

    public ReadOnlyObjectProperty<Phase> phaseProperty() {
        return phase.getReadOnlyProperty();
    }

    public boolean isReadyToMove() {
        return phase.get() == Phase.READY_TO_MOVE;
    }

    public void select(Position position) {
        switch (phase.get()) {
            case SELECT_FROM -> selectFrom(position);
            case SELECT_TO -> selectTo(position);
            case READY_TO_MOVE -> throw new IllegalStateException();
        }
    }

    private void selectFrom(Position position) {
        if (model.isLegalToMoveFrom(position)) {
            from = position;
            phase.set(Phase.SELECT_TO);
            invalidSelection = false;
        } else {
            invalidSelection = true;
        }
    }

    private void selectTo(Position position) {
        if (model.isLegalMove(from, position)) {
            to = position;
            phase.set(Phase.READY_TO_MOVE);
            invalidSelection = false;
        } else {
            invalidSelection = true;
        }
    }

    public Position getFrom() {
        if (phase.get() == Phase.SELECT_FROM) {
            throw new IllegalStateException();
        }
        return from;
    }

    public Position getTo() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        }
        return to;
    }

    public boolean isInvalidSelection() {
        return invalidSelection;
    }

    public void makeMove() {
        if (phase.get() != Phase.READY_TO_MOVE) {
            throw new IllegalStateException();
        }
        model.makeMove(from, to);
        reset();
    }

    public void reset() {
        from = null;
        to = null;
        phase.set(Phase.SELECT_FROM);
        invalidSelection = false;
    }
}
