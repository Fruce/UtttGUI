package sceneControllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import main.ResizableScene;
import main.SceneManager;

public class MainMenuController implements ResizableScene {

    @FXML
    private StackPane menuWrapper; 

    @FXML
    private StackPane menuRoot;
    
    @Override
    public StackPane getWrapper() {
        return menuWrapper;
    }

    @Override
    public StackPane getRoot() {
        return menuRoot;
    }
    
    @Override
    public double getBaseSize() {
        return 600;
    }

    @FXML
    public void offline() {
        SceneManager.switchTo("offline-menu.fxml");
    }

    @FXML
    public void multiplayer() {
        SceneManager.switchTo("multiplayer-menu.fxml");
    }

    @FXML
    public void settings() {
        SceneManager.switchTo("settings.fxml");
    }

    @FXML
    public void quit() {
        Platform.exit();
        System.exit(0);
    }

}
