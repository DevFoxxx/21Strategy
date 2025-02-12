package org.example;  // Il pacchetto deve rimanere lo stesso

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {  // Estendi Application per utilizzare JavaFX

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Carica il file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Pane root = loader.load();  // Usa Pane invece di StackPane

        // Crea la scena utilizzando il layout dal FXML
        Scene scene = new Scene(root, 1010, 630);

        // Imposta il titolo e la scena
        primaryStage.setTitle("21Strategy");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
