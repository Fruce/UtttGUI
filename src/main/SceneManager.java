package main;

import board.BoardMaker;
import game.GameController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public final class SceneManager {

    private static Stage stage;

    private SceneManager() {}

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Ultimate Tic Tac Toe - Fruce");
    }

    public static void switchTo(String fxml) {
        try {
            Parent root = FXMLLoader.load(
                SceneManager.class.getResource("/fxml/" + fxml)
            );
            stage.setScene(new Scene(root, 700, 700));
            stage.show();
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
            stage.setScene(new Scene(root, 700, 700));
            stage.show();

            return loader.getController();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxml, e);
        }
    }


    public static void startGame(GameController controller) {
        try {
            StackPane boardWrapper = BoardMaker.createBoard(controller);
            
            Scene scene = new Scene(boardWrapper, 700, 700);
            scene.getStylesheets().add(
            	    SceneManager.class.getResource("/board/board.css").toExternalForm()
            	);
            
            stage.setScene(scene);
            
            double min = Main.MIN_BOARD_SIZE + Main.OUTER_PADDING * 2;

            stage.setMinWidth(min);
            stage.setMinHeight(min);

            stage.show();

            // padding (was in BoardInitializer)
            boardWrapper.setPadding(new Insets(Main.OUTER_PADDING));

            // resizing logic (was in BoardInitializer)
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

}

