package board;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class EscOverlay {

    public static VBox create(Runnable onLeave) {

        Label title = new Label("Paused");
        title.getStyleClass().add("esc-title");

        Button leave = new Button("Leave Game");
        leave.getStyleClass().add("esc-leave-btn");
        leave.setOnAction(e -> onLeave.run());

        VBox panel = new VBox(40, title, leave);
        panel.setAlignment(Pos.CENTER);
        panel.setMaxWidth(420);
        panel.setPrefWidth(420);
        panel.getStyleClass().add("esc-panel");


        VBox overlay = new VBox(panel);
        overlay.setAlignment(Pos.CENTER);
        overlay.getStyleClass().add("esc-overlay");

        return overlay;
    }
}


