package com.example;

import io.github.cdimascio.dotenv.Dotenv;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloFX extends Application {

    /**
     * Initializes and shows the primary application window by loading "hello-view.fxml".
     *
     * Loads the UI from the "hello-view.fxml" resource, creates a Scene sized 640x480,
     * sets the window title to "Chat", and displays the stage.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws Exception if the FXML resource cannot be loaded or the scene cannot be constructed
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloFX.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}