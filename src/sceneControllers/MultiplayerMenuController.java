package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import main.ResizableScene;
import main.SceneManager;

public class MultiplayerMenuController implements ResizableScene{
	
    @FXML
    private StackPane multiWrapper; 

    @FXML
    private StackPane multiRoot;
    
    @Override
    public StackPane getWrapper() {
        return multiWrapper;
    }

    @Override
    public StackPane getRoot() {
        return multiRoot;
    }
    
    @Override
    public double getBaseSize() {
        return 600;
    }

	
	public void host() {
		GameLauncher.host();
	}

	public void join() {
	    SceneManager.switchTo("join-dialog.fxml");
	}
	
	public void back() {
		SceneManager.switchTo("main-menu.fxml");
	}
		
}
