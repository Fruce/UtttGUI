package sceneControllers;

import game.GameMode;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import main.ResizableScene;
import main.SceneManager;

public class OfflineMenuController implements ResizableScene {
	
    @FXML
    private StackPane offlineWrapper; 

    @FXML
    private StackPane offlineRoot;
    
    @Override
    public StackPane getWrapper() {
        return offlineWrapper;
    }

    @Override
    public StackPane getRoot() {
        return offlineRoot;
    }
    
    @Override
    public double getBaseSize() {
        return 600;
    }
	
	public void startTwoPlayer() {
	    GameLauncher.launch(GameMode.OFFLINE_2P);
	}

	public void startAI() {
	    GameLauncher.launch(GameMode.OFFLINE_AI);
	}

	public void back() {
	    SceneManager.switchTo("main-menu.fxml");
	}

	
}
