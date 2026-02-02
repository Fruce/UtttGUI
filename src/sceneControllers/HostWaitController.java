package sceneControllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.ResizableScene;
import main.SceneManager;
import network.HostServer;
import network.NgrokManager;

public class HostWaitController implements ResizableScene {
	
	@FXML
	private StackPane hostWrapper; 
	
	@FXML
	private StackPane hostRoot;
	
	public StackPane getWrapper() {
	    return hostWrapper;
	}
	
	public StackPane getRoot() {
	    return hostRoot;
	}
	
	public double getBaseSize() {
	    return 600;
	}

    @FXML
    private Label codeLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button copyButton;

    @FXML
    private GridPane codeBox;

    private String joinCode;
    private HostServer hostServer;
       
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

    @FXML
	private void copyCode() {
	
	    String code = codeLabel.getText();
	    if (code == null || code.isBlank()) return;
	
	    ClipboardContent content = new ClipboardContent();
	    content.putString(code);
	    Clipboard.getSystemClipboard().setContent(content);
	
	    copyButton.setText("COPIED");
	    copyButton.setDisable(true);
	    copyButton.setStyle("""
	        -fx-background-color: rgba(245,196,0,0.25);
	        -fx-border-color: #f5c400;
	        -fx-text-fill: #f5c400;
	        -fx-font-weight: bold;
	    """);
	
	    PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
	    pause.setOnFinished(e -> {
	        copyButton.setText("COPY");
	        copyButton.setDisable(false);
	        copyButton.setStyle("""
	            -fx-background-color: transparent;
	            -fx-border-color: #f5c400;
	            -fx-text-fill: #f5c400;
	            -fx-font-weight: bold;
	        """);
	    });
	    pause.play();
	}


    @FXML
    private void leave() {
        NgrokManager.stopTunnel();

        if (hostServer != null) {
            hostServer.stop();
        }

        SceneManager.switchTo("multiplayer-menu.fxml");
    }
}
