package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.ResizableScene;
import main.SceneManager;

public class JoinController implements ResizableScene {

    @FXML
    private StackPane joinWrapper;

    @FXML
    private StackPane joinRoot;
    
    

    @FXML
    private TextField addressField;

    @FXML
    private Button pasteButton;

    @Override
    public StackPane getWrapper() {
        return joinWrapper;
    }

    @Override
    public StackPane getRoot() {
        return joinRoot;
    }

    @Override
    public double getBaseSize() {
        return 600;
    }

    private Tooltip pasteTooltip;
    
    @FXML
    private void initialize() {
        pasteTooltip = new Tooltip("Paste");
        pasteTooltip.setShowDelay(Duration.millis(120));
        pasteTooltip.setHideDelay(Duration.millis(50));
        pasteTooltip.setShowDuration(Duration.seconds(4));
        pasteButton.setTooltip(pasteTooltip);
    }
    
    @FXML
    private void pasteCode() {
        String text = Clipboard.getSystemClipboard().getString();
        if (text == null) return;
        
        if (text.length() <=50)
        		addressField.setText(text);
        
        }
    

    @FXML
    public void connect() {
        String address = addressField.getText().trim();

        GameLauncher.join(address);
    }

    @FXML
    public void back() {
        SceneManager.switchTo("multiplayer-menu.fxml");
    }
}
