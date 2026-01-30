package main;


import board.BoardMaker;
import board.SoundFX;
import game.GameController;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static final double OUTER_PADDING = 24;
    public static final double MIN_BOARD_SIZE = 420;

    @Override
    public void start(Stage stage) {
    		SoundFX.init();

        GameController controller = new GameController();
        StackPane boardWrapper = BoardMaker.createBoard(controller);

        StackPane root = new StackPane(boardWrapper);
        root.setPadding(new Insets(OUTER_PADDING));

        Scene scene = new Scene(root, 700, 700);
        scene.getStylesheets().add(
                getClass().getResource("/board/board.css").toExternalForm()
        );

        /* ========= THE IMPORTANT PART ========= */

        boardWrapper.minWidthProperty().bind(
                Bindings.max(
                        MIN_BOARD_SIZE,
                        Bindings.min(
                                scene.widthProperty().subtract(OUTER_PADDING * 2),
                                scene.heightProperty().subtract(OUTER_PADDING * 2)
                        )
                )
        );

        boardWrapper.minHeightProperty().bind(boardWrapper.minWidthProperty());
        boardWrapper.maxWidthProperty().bind(boardWrapper.minWidthProperty());
        boardWrapper.maxHeightProperty().bind(boardWrapper.minWidthProperty());

        /* ====================================================== */

        stage.setMinWidth(MIN_BOARD_SIZE + OUTER_PADDING * 2 + 150);
        stage.setMinHeight(MIN_BOARD_SIZE + OUTER_PADDING * 2 + 150);
        
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/icon/icon.jpg"))
            );

        stage.setTitle("Ultimate Tic Tac Toe  - Fruce");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
    	
        launch();
    }
}
