package org.example;  // The package must remain the same

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @brief The main class that launches the JavaFX application.
 * @details This class extends {@link Application} to utilize JavaFX.
 */
public class Main extends Application {  // Extend Application to use JavaFX

    /**
     * @brief Main method to start the application.
     * @details Loads the FXML file, creates the scene, and displays it in the primary window.
     * @param primaryStage the primary window of the application
     * @throws IOException if an error occurs while loading the FXML file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Pane root = loader.load();  // Use Pane instead of StackPane

        // Create a scene with the layout loaded from FXML
        Scene scene = new Scene(root, 1010, 550);

        // Set the title for the main window
        primaryStage.setTitle("21Strategy");
        // Set the scene and show the window
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);  // Launch the JavaFX application
    }
}
