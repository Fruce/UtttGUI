package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import main.ResizableScene;
import main.SceneManager;
import utils.UserSettings;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;


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
    
    @FXML
    private Button saveUsernameButton;


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

        // load username
        usernameField.setText(UserSettings.getUsername());
        
        installFastTooltip(saveUsernameButton, "Save");
    }
    
    private void installFastTooltip(Button button, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(120));
        tooltip.setHideDelay(Duration.millis(50));
        tooltip.setShowDuration(Duration.seconds(4));

        tooltip.getStyleClass().add("save-tooltip");
        button.setTooltip(tooltip);
    }


    /* ========================
       SAVE ACTIONS
       ======================== */

   
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
