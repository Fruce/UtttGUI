package board;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import main.SceneManager;
import utils.UserSettings;

public final class SidePane {

    private SidePane() {}

    /* ======================
       STATIC UI REFERENCES
       ====================== */

    private static Label opponentName;
    private static Label yourName;

    private static Text turnText;
    private static Text turnSymbol;

    /* ======================
       PUBLIC API
       ====================== */

    public static void setOpponentName(String name) {
        if (opponentName != null && name != null) {
            opponentName.setText(name);
        }
    }

    /**
     * @param yourTurn true  = highlight your name
     *                 false = highlight opponent name
     */
    public static void setActivePlayer(boolean yourTurn) {
        if (opponentName == null || yourName == null) return;

        opponentName.getStyleClass().remove("active");
        yourName.getStyleClass().remove("active");

        if (yourTurn) {
            yourName.getStyleClass().add("active");
        } else {
            opponentName.getStyleClass().add("active");
        }
    }

    /**
     * Updates the center turn indicator.
     * @param yourTurn whether it's the local player's turn
     * @param symbol   'X' or 'O'
     */
    public static void updateTurnText(boolean yourTurn, char symbol) {
        if (turnText == null || turnSymbol == null) return;

        // Main text
        turnText.setText(
            yourTurn ? "Your turn" : "Waiting for opponent"
        );

        // Symbol
        turnSymbol.setText(String.valueOf(symbol));

        // Reset styles
        turnSymbol.getStyleClass().removeAll("turn-x", "turn-o");
        turnText.getStyleClass().removeAll("turn-x", "turn-o");

        if (symbol == 'X') {
            turnSymbol.getStyleClass().add("turn-x");
            turnText.getStyleClass().add("turn-x");
        } else {
            turnSymbol.getStyleClass().add("turn-o");
            turnText.getStyleClass().add("turn-o");
        }
    }

    /* ======================
       UI CREATION
       ====================== */

    public static VBox create() {

        /* ---------- TOP (OPPONENT) ---------- */

        opponentName = new Label("Opponent");
        opponentName.getStyleClass().add("player-name");

        Button newGameBtn = new Button("New Game");
        newGameBtn.setMaxWidth(Double.MAX_VALUE);
        newGameBtn.getStyleClass().add("new-game-btn");

        VBox top = new VBox(15, opponentName, newGameBtn);
        top.setAlignment(Pos.TOP_CENTER);
        top.setFillWidth(true);

        /* ---------- CENTER (TURN STATUS) ---------- */

        turnText = new Text("Waiting for opponent");
        turnText.getStyleClass().add("turn-text");
        turnText.setTextAlignment(TextAlignment.CENTER);

        turnSymbol = new Text("X");
        turnSymbol.getStyleClass().add("turn-x");

        VBox centerBox = new VBox(6, turnText, turnSymbol);
        centerBox.setAlignment(Pos.CENTER);

        StackPane center = new StackPane(centerBox);
        center.setAlignment(Pos.CENTER);
        VBox.setVgrow(center, Priority.ALWAYS);

        /* ---------- BOTTOM (YOU) ---------- */

        yourName = new Label();
        yourName.getStyleClass().add("player-name");
        yourName.textProperty().bind(UserSettings.usernameProperty());

        Button takebackBtn = new Button("Offer Takeback");
        takebackBtn.setMaxWidth(Double.MAX_VALUE);
        takebackBtn.getStyleClass().add("takeback-btn");

        VBox bottom = new VBox(15, takebackBtn, yourName);
        bottom.setAlignment(Pos.BOTTOM_CENTER);
        bottom.setFillWidth(true);

        /* ---------- ROOT ---------- */

        VBox sidePane = new VBox(16, top, center, bottom);
        sidePane.setPrefWidth(SceneManager.SIDE_PANE_WIDTH);
        sidePane.setMinWidth(SceneManager.SIDE_PANE_WIDTH);
        sidePane.setMaxWidth(SceneManager.SIDE_PANE_WIDTH);
        sidePane.setMaxHeight(Double.MAX_VALUE);
        sidePane.setFillWidth(true);
        sidePane.setAlignment(Pos.CENTER);
        sidePane.getStyleClass().add("side-pane");

        return sidePane;
    }
}
