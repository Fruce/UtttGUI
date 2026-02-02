package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import main.ResizableScene;
import main.SceneManager;
import utils.UserSettings;
import network.NgrokAuthManager;

public class SettingsController implements ResizableScene {

    @FXML
    private StackPane settingsWrapper;

    @FXML
    private StackPane settingsRoot;

    @FXML
    private TextField tokenField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label statusLabel;

    @Override
    public StackPane getWrapper() {
        return settingsWrapper;
    }

    @Override
    public StackPane getRoot() {
        return settingsRoot;
    }

    @Override
    public double getBaseSize() {
        return 600;
    }

    @FXML
    public void initialize() {
        // load ngrok token
        String token = NgrokAuthManager.getToken();
        if (token != null) {
            tokenField.setText(token);
        }

        // load username
        usernameField.setText(UserSettings.getUsername());
    }

    /* ========================
       SAVE ACTIONS
       ======================== */

    @FXML
    private void saveToken() {
        String token = tokenField.getText().trim();

        if (token.isEmpty()) {
            statusLabel.setText("Token cannot be empty");
            return;
        }

        NgrokAuthManager.saveToken(token);
        statusLabel.setText("Ngrok token saved");
    }

    @FXML
    private void saveUsername() {
        String name = usernameField.getText().trim();

        if (name.isEmpty()) {
            statusLabel.setText("Username cannot be empty");
            return;
        }

        UserSettings.setUsername(name);
        statusLabel.setText("Username saved");
    }

    /* ========================
       NAVIGATION
       ======================== */

    @FXML
    private void back() {
        SceneManager.switchTo("main-menu.fxml");
    }
}
