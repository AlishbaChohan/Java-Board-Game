package boardgame.controllers;

import boardgame.model.BoardGameModel;
import boardgame.model.Position;
import boardgame.model.Square;
import boardgame.util.BoardGameMoveSelector;
import game.State;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.tinylog.Logger;

/**
 * Controller class for handling the game logic and user interaction.
 */
public class BoardGameController {
    @FXML
    private GridPane board;

    @FXML
    private Text playerOneText;

    @FXML
    private Text playerTwoText;

    @FXML
    private TextField numberOfMovesField;

    @FXML
    private Text playerTurn;

    private final StringProperty playerOneName = new SimpleStringProperty();
    private final StringProperty playerTwoName = new SimpleStringProperty();
    public String getPlayerOneName() {
        return playerOneName.get();
    }
    public String getPlayerTwoName() {
        return playerTwoName.get();
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName.set(playerOneName);
        Logger.info("Player 1 name is set to {}", playerOneName);
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName.set(playerTwoName);
        Logger.info("Player 2 name is set to {}", playerTwoName);
    }

    private final BoardGameModel model = new BoardGameModel();

    private final BoardGameMoveSelector selector = new BoardGameMoveSelector(model);

    @FXML
    private void handleQuitButton(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void initialize() {
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);
        Logger.info("Player 1 name: {}", playerOneName);
        Logger.info("Player 2 name: {}", playerTwoName);
        playerOneText.textProperty().bind(playerOneName);
        playerTwoText.textProperty().bind(playerTwoName);
        numberOfMovesField.textProperty().bind(model.numberOfMovesProperty().asString());
        playerTurn.textProperty().bind(model.playerProperty().asString().concat("'s turn"));
        model.gameOverProperty().addListener(this::handleGameOver);
    }

    private StackPane createSquare(int row, int col) {
        var square = new StackPane();
        square.getStyleClass().add("square");
        var piece = new Circle(40);
        piece.fillProperty().bind(createSquareBinding(model.squareProperty(row, col)));
        square.getChildren().add(piece);
        square.setOnMouseClicked(this::handleMouseClick);
        return square;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(square);
        var col = GridPane.getColumnIndex(square);
        Logger.info("Click on square ({},{})", row, col);
        selector.select(new Position(row, col));
        if (selector.isReadyToMove()) {
            selector.makeMove();
        }
    }

    private ObjectBinding<Paint> createSquareBinding(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }

            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case RED -> Color.RED;
                    case BLUE -> Color.BLUE;
                };
            }
        };
    }

    private void showSelectionPhaseChange(ObservableValue<? extends BoardGameMoveSelector.Phase> value, BoardGameMoveSelector.Phase oldPhase, BoardGameMoveSelector.Phase newPhase) {
        switch (newPhase) {
            case SELECT_FROM -> {
            }
            case SELECT_TO -> showSelection(selector.getFrom());
            case READY_TO_MOVE -> hideSelection(selector.getFrom());
        }
    }

    private void showSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().add("selected");
    }

    private void hideSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().remove("selected");
    }

    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void handleGameOver(ObservableValue<? extends Boolean> observableValue, boolean oldValue, boolean newValue) {
        if (newValue) {
            Logger.debug("Game over is called");
            Platform.runLater(this::showGameOverAlertAndExit);
        }
    }

    private void showGameOverAlertAndExit() {
        String winnerName = model.getStatus() == State.Status.PLAYER_1_WINS ? playerOneName.get() : playerTwoName.get();
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText(model.getStatus().toString().concat("\nCongratulations: " + winnerName + " !"));
        alert.showAndWait();
        Platform.exit();
    }

}
