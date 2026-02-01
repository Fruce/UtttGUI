package sceneControllers;

import main.SceneManager;

public class MultiplayerMenuController {
	
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
