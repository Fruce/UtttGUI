package main;

import board.SoundFX;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import network.NgrokManager;

public class Main extends Application {

    public static final double OUTER_PADDING = 24;
    public static final double MIN_BOARD_SIZE = 420;

    @Override
    public void start(Stage stage) {
        SoundFX.init();
        SceneManager.init(stage);
        SceneManager.switchTo("main-menu.fxml");
        
        stage.setOnCloseRequest(e -> {
            NgrokManager.stopTunnel();
            Platform.exit();
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
