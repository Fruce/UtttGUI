package main;

import board.BoardMaker;
import board.SidePane;
import game.GameController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public final class SceneManager {

    private static Stage stage;
    public final static double SIDE_PANE_WIDTH = 250;

    private SceneManager() {}

    /* ================= INIT ================= */

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ultimate Tic Tac Toe - Fruce");

        double minWidth =
            SIDE_PANE_WIDTH +
            Main.MIN_BOARD_SIZE +
            Main.OUTER_PADDING * 2;

        double minHeight =
            Main.MIN_BOARD_SIZE +
            Main.OUTER_PADDING * 3;

        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);

        stage.setWidth(930);
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

            return loader.getController();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }

    /* ================= RESIZE FIX ================= */

    private static void applyResizeFix(ResizableScene rs) {

        StackPane wrapper = rs.getWrapper();
        StackPane root = rs.getRoot();
        double BASE_SIZE = rs.getBaseSize();

        // lock design size
        root.setPrefSize(BASE_SIZE, BASE_SIZE);
        root.setMinSize(BASE_SIZE, BASE_SIZE);
        root.setMaxSize(BASE_SIZE, BASE_SIZE);

        Scale scale = new Scale(1, 1);
        scale.setPivotX(BASE_SIZE / 2);
        scale.setPivotY(BASE_SIZE / 2);
        root.getTransforms().add(scale);

        // 🔑 APPLY SCALE IMMEDIATELY using CURRENT STAGE SIZE
        updateScaleFromStage(scale, BASE_SIZE);

        // 🔁 update only when window actually changes size
        stage.widthProperty().addListener((obs, o, n) ->
            updateScaleFromStage(scale, BASE_SIZE)
        );
        stage.heightProperty().addListener((obs, o, n) ->
            updateScaleFromStage(scale, BASE_SIZE)
        );
    }
    
    private static void updateScaleFromStage(Scale scale, double base) {
        double s = Math.min(
            stage.getWidth() / base,
            stage.getHeight() / base
        );
        scale.setX(s);
        scale.setY(s);
    }


    /* ================= GAME ================= */

    public static void startGame(GameController controller) {
        try {
            StackPane boardWrapper = BoardMaker.createBoard(controller);

            Scene scene = new Scene(boardWrapper);
            scene.getStylesheets().add(
                SceneManager.class
                    .getResource("/board/board.css")
                    .toExternalForm()
            );

            stage.setScene(scene);
            stage.show();

            boardWrapper.setPadding(new Insets(Main.OUTER_PADDING));

            boardWrapper.minWidthProperty().bind(
                Bindings.max(
                    Main.MIN_BOARD_SIZE,
                    Bindings.min(
                        scene.widthProperty().subtract(Main.OUTER_PADDING * 2),
                        scene.heightProperty().subtract(Main.OUTER_PADDING * 2)
                    )
                )
            );

            boardWrapper.minHeightProperty().bind(boardWrapper.minWidthProperty());
            boardWrapper.maxWidthProperty().bind(boardWrapper.minWidthProperty());
            boardWrapper.maxHeightProperty().bind(boardWrapper.minWidthProperty());

        } catch (Exception e) {
            throw new RuntimeException("Failed to start game", e);
        }
    }
    
    public static void startMultiplayerGame(GameController controller) {
        try {
            // --- create nodes ---
            StackPane boardWrapper = BoardMaker.createBoard(controller);
            VBox sidePane = SidePane.create();

            HBox root = new HBox(15, sidePane, boardWrapper);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(Main.OUTER_PADDING));

            HBox.setHgrow(boardWrapper, Priority.ALWAYS);
            HBox.setHgrow(sidePane, Priority.NEVER);
            
            

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                SceneManager.class
                    .getResource("/board/board.css")
                    .toExternalForm()
            );

            stage.setScene(scene);
            stage.show();

            /* =============================
               SINGLE SOURCE OF TRUTH
               ============================= */

            DoubleBinding boardSize = Bindings.createDoubleBinding(
                () -> Math.max(
                    Main.MIN_BOARD_SIZE,
                    Math.min(
                        scene.getWidth()
                            - SIDE_PANE_WIDTH
                            - Main.OUTER_PADDING * 2,
                        scene.getHeight()
                            - Main.OUTER_PADDING * 2
                    )
                ),
                scene.widthProperty(),
                scene.heightProperty()
            );

            /* =============================
               APPLY SIZE
               ============================= */

            // board (square)
            boardWrapper.prefWidthProperty().bind(boardSize);
            boardWrapper.prefHeightProperty().bind(boardSize);
            boardWrapper.minWidthProperty().bind(boardSize);
            boardWrapper.minHeightProperty().bind(boardSize);
            boardWrapper.maxWidthProperty().bind(boardSize);
            boardWrapper.maxHeightProperty().bind(boardSize);

            // side pane (locked to board height)
            sidePane.prefHeightProperty().bind(boardSize);
            sidePane.minHeightProperty().bind(boardSize);
            sidePane.maxHeightProperty().bind(boardSize);

        } catch (Exception e) {
            throw new RuntimeException("Failed to start multiplayer game", e);
        }
    }



    

}
