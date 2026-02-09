package board;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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

    private static Button newGameBtn;
    private static Button takebackBtn;

    private static StackPane newGameOfferBar;
    private static StackPane takebackOfferBar;

    private static VBox newGameNormalBox;
    private static VBox newGameOfferSentBox;

    private static VBox takebackNormalBox;
    private static VBox takebackOfferSentBox;

    private static final double ROW_HEIGHT =
            SceneManager.BOARD_DESIGN_SIZE / 6;

    /* ======================
       VISIBILITY HELPERS
       ====================== */

    private static void showOnly(Node visible, Node... hidden) {
        visible.setVisible(true);
        visible.setManaged(true);
        for (Node n : hidden) {
            n.setVisible(false);
            n.setManaged(false);
        }
    }

    /* ======================
       PUBLIC UI STATE API
       ====================== */

    public static void showNormalControls() {
        showOnly(newGameNormalBox, newGameOfferSentBox, newGameOfferBar);
    }

    public static void showNewGameOfferSent() {
        showOnly(newGameOfferSentBox, newGameNormalBox, newGameOfferBar);
    }

    public static void showNewGameOfferReceived() {
        showOnly(newGameOfferBar, newGameNormalBox, newGameOfferSentBox);
    }

    public static void showTakebackNormalControls() {
        showOnly(takebackNormalBox, takebackOfferSentBox, takebackOfferBar);
    }

    public static void showTakebackOfferSent() {
        showOnly(takebackOfferSentBox, takebackNormalBox, takebackOfferBar);
    }

    public static void showTakebackOfferReceived() {
        showOnly(takebackOfferBar, takebackNormalBox, takebackOfferSentBox);
    }

    /* ======================
       TURN / PLAYER STATUS
       ====================== */

    public static void setOpponentName(String name) {
        opponentName.setText(name);
    }

    public static void setActivePlayer(boolean yourTurn) {
        opponentName.getStyleClass().remove("active");
        yourName.getStyleClass().remove("active");
        (yourTurn ? yourName : opponentName).getStyleClass().add("active");
    }

    public static void updateTurnText(boolean yourTurn, char symbol) {
        turnSymbol.getStyleClass().clear();
        turnText.getStyleClass().clear();

        if (!yourTurn) {
            turnText.setText("Waiting for opponent...");
            turnText.getStyleClass().add("turn-status");
            turnSymbol.setText("");
            return;
        }

        turnText.setText("Your turn");
        turnText.getStyleClass().add(symbol == 'X' ? "turn-x" : "turn-o");
        turnSymbol.setText(String.valueOf(symbol));
        turnSymbol.getStyleClass().add(symbol == 'X' ? "turn-x" : "turn-o");
    }

    /* ======================
       BUTTON ACCESSORS
       ====================== */

    public static Button getNewGameButton() { return newGameBtn; }
    public static Button getTakebackButton() { return takebackBtn; }

    public static Label getNewGameAccept() {
        return (Label) ((HBox) newGameOfferBar.getChildren().get(0)).getChildren().get(2);
    }

    public static Label getNewGameDeny() {
        return (Label) ((HBox) newGameOfferBar.getChildren().get(0)).getChildren().get(0);
    }

    public static Label getTakebackAccept() {
        return (Label) ((HBox) takebackOfferBar.getChildren().get(0)).getChildren().get(2);
    }

    public static Label getTakebackDeny() {
        return (Label) ((HBox) takebackOfferBar.getChildren().get(0)).getChildren().get(0);
    }

    /* ======================
       UI CREATION
       ====================== */

    public static VBox create() {

        opponentName = makeNameLabel("Opponent");
        yourName = makeNameLabel("");
        yourName.textProperty().bind(UserSettings.usernameProperty());

        /* ---------- NEW GAME ---------- */

        newGameBtn = makeActionButton("New Game");
        newGameBtn.getStyleClass().add("new-game-btn");
        
        newGameNormalBox = new VBox(newGameBtn);

        newGameOfferSentBox = makeSentButtonBox("New Game Offer Sent");

        newGameOfferBar =
                makeOfferBar("Opponent offered\nNew Game");

        StackPane newGameStack = new StackPane(
                newGameNormalBox,
                newGameOfferSentBox,
                newGameOfferBar
        );

        showNormalControls();

        VBox top = new VBox(opponentName, newGameStack);

        /* ---------- TURN ---------- */

        turnText = new Text("Waiting for opponent...");
        turnText.setTextAlignment(TextAlignment.CENTER);
        turnText.getStyleClass().add("turn-status");

        turnSymbol = new Text("X");
        turnSymbol.getStyleClass().add("turn-x");

        VBox centerBox = new VBox(6, turnText, turnSymbol);
        centerBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(centerBox, Priority.ALWAYS);

        /* ---------- TAKEBACK ---------- */

        takebackBtn = makeActionButton("Offer Takeback");
        takebackBtn.getStyleClass().add("takeback-btn");
        takebackNormalBox = new VBox(takebackBtn);

        takebackOfferSentBox = makeSentButtonBox("Takeback Offer Sent");

        takebackOfferBar =
                makeOfferBar("Opponent offered\nTakeback");

        StackPane takebackStack = new StackPane(
                takebackNormalBox,
                takebackOfferSentBox,
                takebackOfferBar
        );

        showTakebackNormalControls();

        VBox bottom = new VBox(takebackStack, yourName);

        VBox root = new VBox(top, centerBox, bottom);
        root.setPrefWidth(SceneManager.SIDE_PANE_WIDTH);
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("side-pane");

        return root;
    }

    /* ======================
       HELPERS
       ====================== */

    private static Label makeNameLabel(String text) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setMinHeight(ROW_HEIGHT);
        l.setAlignment(Pos.CENTER);
        l.getStyleClass().add("player-name");
        return l;
    }

    private static Button makeActionButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setMinHeight(ROW_HEIGHT);
        return b;
    }

    private static VBox makeSentButtonBox(String text) {
        Button b = makeActionButton(text);
        b.setDisable(true);
        return new VBox(b);
    }

    private static StackPane makeOfferBar(String text) {

        Label deny = iconLabel("/icons/cross.png");
        Label accept = iconLabel("/icons/tick.png");

        Region spacer = new Region();
        spacer.setMinWidth(12);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox clickLayer = new HBox(deny, spacer, accept);
        clickLayer.setAlignment(Pos.CENTER);
        clickLayer.setMaxWidth(Double.MAX_VALUE);

        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setTextAlignment(TextAlignment.CENTER);
        msg.setAlignment(Pos.CENTER);
        msg.getStyleClass().add("offer-text");
        msg.setTranslateY(-6);

        StackPane bar = new StackPane(clickLayer, msg);
        bar.setMinHeight(ROW_HEIGHT);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.getStyleClass().add("offer-bar");

        StackPane.setAlignment(msg, Pos.CENTER);

        return bar;
    }

    private static Label iconLabel(String path) {
        ImageView iv = new ImageView(
                new Image(SidePane.class.getResourceAsStream(path))
        );
        iv.setPreserveRatio(true);
        iv.setFitHeight(64);

        Label l = new Label("", iv);
        l.setAlignment(Pos.CENTER);
        l.getStyleClass().add("offer-icon");
        l.setMinWidth(64);
        l.setMinHeight(ROW_HEIGHT);

        return l;
    }
}
