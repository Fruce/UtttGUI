package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class JoinController {

    @FXML
    private TextField addressField;

    @FXML
    public void connect() {
        String address = addressField.getText().trim();

        if (address.isEmpty()) {
            System.out.println("[JOIN] Address is empty");
            return;
        }

        GameLauncher.join(address);
    }

    @FXML
    public void back() {
        main.SceneManager.switchTo("multiplayer-menu.fxml");
    }
}
