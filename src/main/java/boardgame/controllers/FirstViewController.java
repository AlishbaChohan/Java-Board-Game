package boardgame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;

import java.io.IOException;

public class FirstViewController {
    @FXML
    private TextField playerOneNameField;

    @FXML
    private TextField playerTwoNameField;

    @FXML
    private void handleNextButton(ActionEvent event) throws IOException {
        Logger.info("Player 1 Name entered: {}", playerOneNameField.getText());
        Logger.info("Player 2 Name entered: {}", playerTwoNameField.getText());
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        Parent root = fxmlLoader.load();
        BoardGameController controller = fxmlLoader.getController();
        controller.setPlayerOneName(playerOneNameField.getText());
        controller.setPlayerTwoName(playerTwoNameField.getText());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
