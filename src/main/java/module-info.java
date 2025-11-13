module hellofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires tools.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires io.github.cdimascio.dotenv.java;
    requires javafx.graphics;

    opens com.example to javafx.fxml;
    exports com.example;
}