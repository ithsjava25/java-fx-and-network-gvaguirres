package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel();

    @FXML
    private TextArea chatArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;

    @FXML
    public AnchorPane rootPane;

    @FXML
    private void initialize() {

    }


    public void onSendButtonClicked(ActionEvent actionEvent) {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            chatArea.appendText("You: " + message + "\n");
            messageField.clear();
        }
        //regNumberField.setStyle("-fx-border-color: red;");
    }
}
