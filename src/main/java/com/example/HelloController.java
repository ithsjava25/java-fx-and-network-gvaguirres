package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

/**
 * Controller layer: mediates between the view (FXML) and the model.
 */
public class HelloController {

    private final NtfyConnection ntfyConnection = new NtfyConnectionImpl();
    private final HelloModel model = new HelloModel(ntfyConnection);

    @FXML
    public ListView<NtfyMessageDto> messageView;
    @FXML
    public TextArea chatArea;
    @FXML
    private TextArea messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button attachButton;

    @FXML
    private void initialize() {
        model.initialize();
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

    public void handleAttachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images (JPEG, PNG)", "*.jpg", "*.jpeg", "*.png"),
                new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx"),
                new FileChooser.ExtensionFilter("Files", "*.*")
        );
        Window stage = attachButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            handleSendFile(file);
        }
    }
    private void  handleSendFile(File file) {
        ntfyConnection.sendImage(file)
                .thenAccept(response -> {
                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        System.out.println("File sent successfully: " + file.getName());
                    } else {
                        System.out.println("Failed to send file: " + response.statusCode());
                    }
                })
                .exceptionally(e -> {
                    System.out.println("Error sending file: " + e.getMessage());
                    return null;
                });
    }
}
