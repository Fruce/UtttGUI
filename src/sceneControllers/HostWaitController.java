package sceneControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.ResizableScene;
import main.SceneManager;
import network.CloudflareTunnel;
import network.HostServer;

public class HostWaitController implements ResizableScene {

    @FXML
    private StackPane hostWrapper;

    @FXML
    private StackPane hostRoot;

    @FXML
    private TextField codeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button copyButton;

    @FXML
    private GridPane codeBox;

    private String joinCode;
    private HostServer hostServer;

    private Tooltip copyTooltip;

    // ===== ResizableScene =====
    @Override
    public StackPane getWrapper() {
        return hostWrapper;
    }

    @Override
    public StackPane getRoot() {
        return hostRoot;
    }

    @Override
    public double getBaseSize() {
        return 600;
    }

    // ===== Init =====
    @FXML
    private void initialize() {
        copyTooltip = new Tooltip("Copy");
        copyTooltip.setShowDelay(Duration.millis(120)); // fast
        copyTooltip.setHideDelay(Duration.millis(50));
        copyTooltip.setShowDuration(Duration.seconds(4));
        copyButton.setTooltip(copyTooltip);
    }

    // ===== UI State =====
    public void showStarting() {
        codeLabel.setText("Starting server...");
    }

    public void setJoinCode(String code) {
        this.joinCode = code;
        codeLabel.setText(code);

        statusLabel.setText("Waiting for player to connect...");
        codeBox.setVisible(true);
        codeBox.setManaged(true);
    }

    public void setHostServer(HostServer hostServer) {
        this.hostServer = hostServer;
    }

    // ===== Actions =====
    @FXML
    private void copyCode() {
        String code = codeLabel.getText();
        if (code == null || code.isBlank()) return;

        ClipboardContent content = new ClipboardContent();
        content.putString(code);
        Clipboard.getSystemClipboard().setContent(content);

        copyButton.setDisable(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            copyButton.setDisable(false);
            copyTooltip.setText("Copy");
        });
        pause.play();
    }

    @FXML
    private void leave() throws InterruptedException {
        CloudflareTunnel.stopTunnel();

        if (hostServer != null) {
            hostServer.stop();
        }

        SceneManager.switchTo("multiplayer-menu.fxml");
    }
}
