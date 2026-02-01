package sceneControllers;

import game.GameMode;
import main.SceneManager;

public class OfflineMenuController {
	
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
