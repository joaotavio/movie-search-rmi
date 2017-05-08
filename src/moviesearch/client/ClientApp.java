package moviesearch.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    private static final String TITLE = "Movie Search";
    private static final int MIN_WIDTH = 600;
    private static final int MIN_HEIGHT = 640;

    @Override
    public void start(Stage stage) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("view/main.fxml"));

            Scene scene = new Scene(root);

            stage.setMinHeight(MIN_HEIGHT);
            stage.setMinWidth(MIN_WIDTH);

            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
