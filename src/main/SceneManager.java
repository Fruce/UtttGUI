package main;

import board.BoardMaker;
import board.EscOverlay;
import board.SidePane;
import game.GameController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import network.NetworkEndpoint;
import network.NetworkListener;
import sceneControllers.SidePaneController;

public final class SceneManager {

    private static Stage stage;

    /* ================= DESIGN CONSTANTS ================= */

    public static final double SIDE_PANE_WIDTH = 340;

    // BoardMaker: BOARD_DESIGN_SIZE = 260 -> 260 * 3
    public static final double BOARD_DESIGN_SIZE = 780;

    public static final double GAME_HEIGHT = BOARD_DESIGN_SIZE;
    public static final double GAME_WIDTH =
            SIDE_PANE_WIDTH + BOARD_DESIGN_SIZE;

    private SceneManager() {}

    /* ================= INIT ================= */

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ultimate Tic Tac Toe - Fruce");

        stage.setMinWidth(GAME_WIDTH * 0.75);
        stage.setMinHeight(GAME_HEIGHT * 0.75);

        stage.setWidth(973);
        stage.setHeight(700);
    }

    /* ================= SCENE SWITCH ================= */

    public static void switchTo(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/fxml/" + fxml)
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

            Object controller = loader.getController();

            if (controller instanceof ResizableScene rs) {
                applyResizeFix(rs);
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }

    
    public static <T> T switchToAndGetController(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                SceneManager.class.getResource("/fxml/" + fxml)
            );

            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();

            Object controller = loader.getController();

            if (controller instanceof ResizableScene rs) {
                applyResizeFix(rs);
            }

            return (T) controller;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }

    /* ================= GAME SCENE CORE ================= */

    private static void showGameScene(Parent scalableGameRoot) {

	    // Layer 1: holds the scaled content
	    StackPane scaleHolder = new StackPane(scalableGameRoot);
	    scaleHolder.setAlignment(Pos.CENTER);
	
	    // Layer 2: padding layer (NOT scaled)
	    StackPane paddedRoot = new StackPane(scaleHolder);
	    paddedRoot.setPadding(new Insets(Main.OUTER_PADDING));
	    paddedRoot.setAlignment(Pos.CENTER);
	    	
	    // Layer 3: overlay layer (ESC menu etc.)
	    StackPane overlayRoot = new StackPane(paddedRoot);
	
	    Scene scene = new Scene(overlayRoot);
	
	    scene.getStylesheets().add(
	        SceneManager.class
	            .getResource("/board/board.css")
	            .toExternalForm()
	    );
	
	    stage.setScene(scene);
	    stage.show();
	
	    installEscOverlay(scene, overlayRoot, scalableGameRoot);
    }



    /* ================= SCALING (SINGLE SOURCE OF TRUTH) ================= */

    private static StackPane makeScalableRoot(
            Parent content,
            double baseWidth,
            double baseHeight
    ) {
        StackPane root = new StackPane(content);

        root.setPrefSize(baseWidth, baseHeight);
        root.setMinSize(baseWidth, baseHeight);
        root.setMaxSize(baseWidth, baseHeight);

        Scale scale = new Scale(1, 1);
        scale.setPivotX(baseWidth / 2);
        scale.setPivotY(baseHeight / 2);
        root.getTransforms().add(scale);

        Runnable updateScale = () -> {
        	
	        	double availableWidth  = stage.getWidth()  - Main.OUTER_PADDING * 2;
	        	double availableHeight = stage.getHeight() - Main.OUTER_PADDING * 2;
	
	        	double s = Math.min(
	        	    availableWidth  / baseWidth,
	        	    availableHeight / baseHeight
	        	);
            
            scale.setX(s);
            scale.setY(s);
        };

        updateScale.run();
        stage.widthProperty().addListener((o,a,b) -> updateScale.run());
        stage.heightProperty().addListener((o,a,b) -> updateScale.run());

        return root;
    }
    
    private static void applyResizeFix(ResizableScene rs) {

        StackPane root = rs.getRoot();
        double BASE_SIZE = rs.getBaseSize();

        root.setPrefSize(BASE_SIZE, BASE_SIZE);
        root.setMinSize(BASE_SIZE, BASE_SIZE);
        root.setMaxSize(BASE_SIZE, BASE_SIZE);

        Scale scale = new Scale(1, 1);
        scale.setPivotX(BASE_SIZE / 2);
        scale.setPivotY(BASE_SIZE / 2);
        root.getTransforms().add(scale);

        Runnable update = () -> {
            double s = Math.min(
                stage.getWidth() / BASE_SIZE,
                stage.getHeight() / BASE_SIZE
            );
            scale.setX(s);
            scale.setY(s);
        };

        update.run();
        stage.widthProperty().addListener((o,a,b) -> update.run());
        stage.heightProperty().addListener((o,a,b) -> update.run());
    }


    /* ================= OFFLINE GAME ================= */

    public static void startGame(GameController controller) {
        try {
            StackPane board = BoardMaker.createBoard(controller);

            StackPane scalableRoot =
                makeScalableRoot(
                    board,
                    BOARD_DESIGN_SIZE,
                    BOARD_DESIGN_SIZE
                );

            showGameScene(scalableRoot);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start game", e);
        }
    }

    /* ================= MULTIPLAYER GAME ================= */

    public static void startMultiplayerGame(
    			GameController controller,
            NetworkListener listener,
            NetworkEndpoint network) 
    {
        try {
            StackPane board = BoardMaker.createBoard(controller);
            StackPane.setAlignment(board, Pos.CENTER);

            var sidePane = SidePane.create();

            SidePaneController sidePaneController =
                    new SidePaneController(controller, listener, network);
            
            controller.setOnNewGameOfferReceived(
                    sidePaneController::onNewGameOfferReceived
            );

            controller.setOnNewGameAccepted(
                    sidePaneController::onNewGameAccepted
            );

            controller.setOnNewGameDenied(
                    sidePaneController::onNewGameDenied
            );
            
            // ====== TAKEBACK =====
            
            controller.setOnTakebackOfferReceived(
                    sidePaneController::onTakebackOfferReceived
            );

            controller.setOnTakebackAccepted(
                    sidePaneController::onTakebackAccepted
            );

            controller.setOnTakebackDenied(
                    sidePaneController::onTakebackDenied
            );
            
            //avoids Highlight border fucking up the SidePane buttons adjacent to it.
            sidePane.setViewOrder(-1);
            
            HBox layout = new HBox(15, sidePane, board);
            layout.setAlignment(Pos.CENTER);
            layout.setPadding(new Insets(0));

            StackPane scalableRoot =
                makeScalableRoot(
                    layout,
                    GAME_WIDTH,
                    GAME_HEIGHT
                );

            showGameScene(scalableRoot);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start multiplayer game", e);
        }
    }

    /* ================= ESC OVERLAY ================= */

    private static void installEscOverlay(
            Scene scene,
            StackPane overlayRoot,
            Parent gameRoot
    ) {
        var escMenu = EscOverlay.create(() ->
            switchTo("main-menu.fxml")
        );

        escMenu.setVisible(false);
        escMenu.setManaged(false);

        overlayRoot.getChildren().add(escMenu);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ESCAPE -> {
                    boolean show = !escMenu.isVisible();

                    escMenu.setVisible(show);
                    escMenu.setManaged(show);

                    gameRoot.setEffect(
                        show ? new GaussianBlur(18) : null
                    );
                }
            }
        });
    }
}
