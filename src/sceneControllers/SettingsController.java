package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import main.SceneManager;
import network.NgrokAuthManager;

public class SettingsController {

    @FXML
    private TextField tokenField;

    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        String token = NgrokAuthManager.getToken();
        if (token != null) {
            tokenField.setText(token);
        }
    }

    @FXML
    private void save() {
        String token = tokenField.getText().trim();

        if (token.isEmpty()) {
            statusLabel.setText("Token cannot be empty");
            return;
        }

        NgrokAuthManager.saveToken(token);
        statusLabel.setText("Saved successfully");
    }

    @FXML
    private void back() {
        SceneManager.switchTo("main-menu.fxml");
    }
}
