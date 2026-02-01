package sceneControllers;

import javafx.application.Platform;
import main.SceneManager;

public class MainMenuController {

	public void offline() {
	    SceneManager.switchTo("offline-menu.fxml");
	}

	public void multiplayer() {
	    SceneManager.switchTo("multiplayer-menu.fxml");
	}
	
	public void settings() {
	    SceneManager.switchTo("settings.fxml");
	}

		public void quit() {
		
		Platform.exit();
		System.exit(0);
	}
	
}
