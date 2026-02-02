package sceneControllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import main.ResizableScene;

public class JoinController implements ResizableScene {

	@FXML
	private StackPane joinWrapper; 
	
	@FXML
	private StackPane joinRoot;
	
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

    @FXML
    public TextField addressField;
    
    @FXML
    private void initialize() {

        addressField.setPromptText("Enter 6 letter code");

        addressField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();

//            // Max length 6
            if (newText.length() > 6) {           	
            		return null;               
            }

            // Letters only
            if (!newText.matches("[A-Za-z]*")) {
                return null; // invalid input disappears
            }

            // Force uppercase
            change.setText(change.getText().toUpperCase());

            return change;
        }));
    }

    
    @FXML
    public void connect() {
        String address = addressField.getText().trim();

        if (address.isEmpty() || address.length() !=6) {
            System.out.println("invalid [JOIN] Address");
            
            addressField.clear();
            return;
        }

        GameLauncher.join(address);
    }

    @FXML
    public void back() {
        main.SceneManager.switchTo("multiplayer-menu.fxml");
    }
}
