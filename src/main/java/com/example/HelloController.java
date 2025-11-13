package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final HelloModel model = new HelloModel(new NtfyConnectionImpl());
    public ListView<NtfyMessageDto> messageView;

    @FXML
    public TextArea chatArea;

    @FXML
    private TextArea messageField;
    @FXML
    private Button sendButton;

    @FXML
    private void initialize() {
        messageView.setItems(model.getMessages());
        messageField.textProperty().bindBidirectional(model.messageToSendProperty());

        messageView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(NtfyMessageDto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.message());
            }
        });
    }

    public void sendMessage(ActionEvent actionEvent) {
        model.sendMessage();
    }
}
