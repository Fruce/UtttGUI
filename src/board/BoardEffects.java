package board;

import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;


public class BoardEffects {

	private static int currentBigIndex = -1;
		
    private static final String X_GLOW   = "red-glow";
    private static final String X_BORDER = "red-border-highlight";

    private static final String O_GLOW   = "gold-glow";
    private static final String O_BORDER = "gold-border-highlight";
    
    private static final BoxBlur BLUR = new BoxBlur(0, 0, 3);


    private static String glowFor(char mark) {
        return mark == 'X' ? X_GLOW : O_GLOW;
    }

    private static String borderFor(char mark) {
        return mark == 'X' ? X_BORDER : O_BORDER;
    }
    
    public static BoxBlur getBlurEffect() {
        return BLUR;
    }

    public static void placeMark(StackPane cell, char mark) {
    		
    		//showGameOver(mark);
    	
    		cell.getStyleClass().remove("cell-hover");
        Text markNode = (Text) cell.getChildren().get(0);
        
        markNode.setText(String.valueOf(mark));

        markNode.getStyleClass().removeAll("mark-x", "mark-o");
        markNode.getStyleClass().add(mark == 'X' ? "mark-x" : "mark-o");
        

        BoardAnimations.playMarkPlace(markNode);        
    }

    public static void highlightBigCell(int nextBigIndex, char mark) {

        // grab new highlight/cell first
        Pane newHighlight = BoardMaker.bigHighlight(nextBigIndex)[0];
        Pane newCell      = BoardMaker.bigHighlight(nextBigIndex)[2];

        // SAME board -> refresh only
        if (currentBigIndex == nextBigIndex) {

            newHighlight.getStyleClass().removeAll(X_BORDER, O_BORDER);
            newHighlight.getStyleClass().add(borderFor(mark));

            newCell.getStyleClass().removeAll(X_GLOW, O_GLOW);
            newCell.getStyleClass().add(glowFor(mark));

            // replay enter animation for feedback
            BoardAnimations.playHighlightEnter(newHighlight);
            return;
        }

        // DIFFERENT board → exit old
        if (currentBigIndex != -1) {
            Pane oldHighlight = BoardMaker.bigHighlight(currentBigIndex)[0];
            Pane oldCell      = BoardMaker.bigHighlight(currentBigIndex)[2];

            BoardAnimations.playHighlightExit(oldHighlight);
            oldCell.getStyleClass().removeAll(X_GLOW, O_GLOW);
        }

        // enter new
        newHighlight.getStyleClass().removeAll(X_BORDER, O_BORDER);
        newHighlight.getStyleClass().add(borderFor(mark));
        newHighlight.setVisible(true);

        newCell.getStyleClass().add(glowFor(mark));
        BoardAnimations.playHighlightEnter(newHighlight);

        currentBigIndex = nextBigIndex;
    }




    public static void clearSmallHighlight() {

        if (currentBigIndex == -1) return;

        Pane highlight = BoardMaker.bigHighlight(currentBigIndex)[0];
        Pane cell = BoardMaker.bigHighlight(currentBigIndex)[2];

        BoardAnimations.playHighlightExit(highlight);
        cell.getStyleClass().removeAll(X_GLOW, O_GLOW);

        currentBigIndex = -1;
    }
    
    public static void freeMoveHighlight(char mark) {

        Pane border = BoardMaker.getFreeMoveHighlight();
        StackPane boardRoot = BoardMaker.getBoardRoot();

        border.getStyleClass().removeAll(X_BORDER, O_BORDER);

        border.getStyleClass().add(borderFor(mark));
        boardRoot.getStyleClass().add(glowFor(mark));
        
        border.setVisible(true);
        
        BoardAnimations.playFreeMoveHighlight(border);
    }


    public static void clearFreeMoveHighlight() {
    	
        Pane border = BoardMaker.getFreeMoveHighlight();
        StackPane boardRoot = BoardMaker.getBoardRoot();
        
        boardRoot.getStyleClass().removeAll(X_GLOW, O_GLOW);
        //border.getStyleClass().removeAll(X_BORDER, O_BORDER);
        
        BoardAnimations.stopFreeMoveHighlight(border);
        
    }

    public static void showLocalWin(
            StackPane gameplayLayer,
            Text localWinMark,
            char mark
    ) {
        // 1. Hide the won smallBoard
    		//gameplayLayer.setVisible(false);
    		BoardAnimations.fadeOutSmallGrid(gameplayLayer);

        // 2. Show result
    		localWinMark.setText(String.valueOf(mark));
    		localWinMark.getStyleClass().add(mark == 'O' ? "red-local-win-mark" : "gold-local-win-mark");
    		localWinMark.setVisible(true);
    		
    		BoardAnimations.playLocalWinMark(localWinMark);

    }
    
    public static void showGameOver(char winner) {

        StackPane boardRoot = BoardMaker.getBoardRoot();

        BoardMaker.getGameOverWinner().setText(String.valueOf(winner));
        BoardMaker.getGameOverWins().setText("Wins!");

        BLUR.setWidth(0);
        BLUR.setHeight(0);

        boardRoot.setEffect(BLUR);
        BoardMaker.getGameOverOverlay().setVisible(true);

        BoardAnimations.playGameOver();          // text animation
        BoardAnimations.playGameOverBlur();      // blur animation
    }

}
