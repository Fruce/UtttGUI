package board;

import game.GameController;
import game.MoveResult;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Scale;
import move.MoveSwitcher;

public class BoardMaker {

    // ================= BOARD GEOMETRY =================
    private static final double BIG_CELL_SIZE = 260;
    private static final double BOARD_SIZE = BIG_CELL_SIZE * 3;

    // ================= INNER LAYOUT ==================
    private static final double INNER_GAP = 18;
    private static final double HIGHLIGHT_INSET = 12;

    // ================= ROOT ==========================
    private static StackPane boardRoot;

    // ================= GAME OVER OVERLAY =============
    private static StackPane gameOverOverlay;
    private static Text gameOverTitle;
    private static Text gameOverWinner;
    private static Text gameOverWins;

    // ================= BOARD NODES ===================
    private static GridPane boardInputLayer;
    private static Pane[] enterHighlights = new Pane[9];
    private static Pane[] exitHighlights  = new Pane[9];

    public static final Pane[] bigCells = new Pane[9];
    public static final StackPane[][] cells =  new StackPane[9][9];
    
    private static final StackPane[] gameplayLayers = new StackPane[9];
    private static final Text[] localWinMarks = new Text[9];

    private static Pane freeMoveHighlight;

    // ================= GETTERS =======================

    public static StackPane getBoardRoot() {
        return boardRoot;
    }

    public static StackPane getGameOverOverlay() {
        return gameOverOverlay;
    }

    public static Text getGameOverTitle() {
        return gameOverTitle;
    }

    public static Text getGameOverWinner() {
        return gameOverWinner;
    }

    public static Text getGameOverWins() {
        return gameOverWins;
    }

    public static Pane getFreeMoveHighlight() {
        return freeMoveHighlight;
    }

    public static Pane[] bigHighlight(int index) {
        if (index < 0 || index >= 9) return null;

        return new Pane[]{
            enterHighlights[index],
            exitHighlights[index],
            bigCells[index]
        };
    }

    // =================================================

    public static StackPane createBoard(GameController controller) {

        boardRoot = createBoardRoot();
        GridPane board = createBoardGrid(controller);

        Pane bigLines = createGlobalBigGridLines();
        freeMoveHighlight = createFreeMoveHighlight();

        boardRoot.getChildren().addAll(bigLines, board, freeMoveHighlight);

        StackPane wrapper = createScaledWrapper(boardRoot);

        gameOverOverlay = createGameOverOverlay();
        wrapper.getChildren().add(gameOverOverlay);

        return wrapper;
    }


    /* ================= BIG BOARD ================= */

    private static StackPane createBigBoard(int bigIndex, GameController controller) {

        StackPane gameplayLayer = new StackPane();
        gameplayLayer.setPrefSize(BIG_CELL_SIZE, BIG_CELL_SIZE);
        gameplayLayer.setMouseTransparent(false);

        Pane enter = createHighlightPane();
        Pane exit  = createHighlightPane();
        Pane smallLines = createSmallGridLines();
        GridPane input = createInteractionGrid(bigIndex, controller);
        
        enterHighlights[bigIndex] = enter;
        exitHighlights[bigIndex]  = exit;

        gameplayLayer.getChildren().addAll(smallLines, input, enter, exit);

        Text localWinMark = createLocalWinMark(bigIndex, gameplayLayer);

        StackPane big = new StackPane(gameplayLayer, localWinMark);
        big.setPrefSize(BIG_CELL_SIZE, BIG_CELL_SIZE);

        return big;
    }


    /* ================= GLOBAL BIG LINES ================= */

    private static Pane createGlobalBigGridLines() {

        Pane p = new Pane();
        p.setMouseTransparent(true);

        double s = BOARD_SIZE / 3;

        Line v1 = new Line(s, 0, s, BOARD_SIZE);
        Line v2 = new Line(s * 2, 0, s * 2, BOARD_SIZE);
        Line h1 = new Line(0, s, BOARD_SIZE, s);
        Line h2 = new Line(0, s * 2, BOARD_SIZE, s * 2);

        for (Line l : new Line[]{v1, v2, h1, h2}) {
            l.getStyleClass().add("big-line");
        }

        p.getChildren().addAll(v1, v2, h1, h2);
        return p;
    }

    /* ================= SMALL GRID LINES ================= */

    private static Pane createSmallGridLines() {

        Pane p = new Pane();
        p.setMouseTransparent(true);

        double w = BIG_CELL_SIZE - INNER_GAP * 2;
        double h = w;

        Line v1 = new Line(w / 3, 0, w / 3, h);
        Line v2 = new Line(w * 2 / 3, 0, w * 2 / 3, h);
        Line h1 = new Line(0, h / 3, w, h / 3);
        Line h2 = new Line(0, h * 2 / 3, w, h * 2 / 3);

        p.setTranslateX(INNER_GAP);
        p.setTranslateY(INNER_GAP);

        for (Line l : new Line[]{v1, v2, h1, h2}) {
            l.getStyleClass().add("small-line");
        }

        p.getChildren().addAll(v1, v2, h1, h2);
        return p;
    }

    /* ================= INTERACTION GRID ================= */

    private static GridPane createInteractionGrid(int bigIndex, GameController controller) {

        GridPane grid = new GridPane();

        double usable = BIG_CELL_SIZE - INNER_GAP * 2;
        double cellSize = usable / 3;

        grid.setPrefSize(usable, usable);
        grid.setTranslateX(INNER_GAP);
        grid.setTranslateY(INNER_GAP);

        for (int i = 0; i < 3; i++) {
            grid.getColumnConstraints().add(new ColumnConstraints(cellSize));
            grid.getRowConstraints().add(new RowConstraints(cellSize));
        }

        for (int i = 0; i < 9; i++) {

            int smallIndex = i;

            StackPane cell = new StackPane();
            cell.setPrefSize(cellSize, cellSize);
            cell.setAlignment(Pos.CENTER);

            Text mark = new Text();
            cell.getChildren().add(mark);
            
            cells[bigIndex][smallIndex] = cell;

            cell.setOnMouseClicked(e -> {

	            	MoveResult result = controller.placeMove(bigIndex, smallIndex); 
                refreshBoard(controller, bigIndex, smallIndex, result);
                
                SidePane.setActivePlayer(controller.isLocalPlayersTurn());
                char oppMark = MoveSwitcher.switchMove(controller.getCurrentMark());
                SidePane.updateTurnText(false, oppMark);
                
                updateInputLock(controller); //lock board if not local player.
            });
            
            
            
            cell.setOnMouseEntered(e -> {
            		if (controller.firstMoveLocked) return;
            		
                if (controller.checkValidity(bigIndex, smallIndex) == MoveResult.VALID) {
                
                    SoundFX.playHover();                   
                    BoardAnimations.hoverEnter(cell);
                }
            });

            cell.setOnMouseExited(e -> {
	            	if (controller.firstMoveLocked) return;
	            	BoardAnimations.hoverExit(cell);});

            grid.add(cell, i % 3, i / 3);
        }

        return grid;
    }

    /* ================= REFRESH BOARD ================= */
    
    public static void refreshBoard(
    		GameController controller, int bigIndex, int smallIndex, MoveResult result) 
    {
    	
        char playedMark = controller.getCurrentMark();
        if (result == MoveResult.INVALID) return;
  
        //click sound fx
        if (result != MoveResult.LOCAL_WIN && result != MoveResult.GLOBAL_WIN) {
        		if (playedMark == 'X')
        			SoundFX.playClick_X();
        		else
        			SoundFX.playClick_O();
        		
        }
        BoardEffects.placeMark(cells[bigIndex][smallIndex], playedMark);
        
        BoardEffects.clearFreeMoveHighlight();

        if (result == MoveResult.LOCAL_WIN) {
        		
        		if (playedMark == 'X')
        			SoundFX.playWinLocal_X();
        		else
        			SoundFX.playWinLocal_O();
        		
            BoardEffects.showLocalWin(
                    gameplayLayers[bigIndex],
                    localWinMarks[bigIndex],
                    playedMark
            );
        }

        if (result == MoveResult.GLOBAL_WIN) {
            BoardEffects.clearSmallHighlight();
            SoundFX.playWinGlobal();
            BoardEffects.showLocalWin(
                    gameplayLayers[bigIndex],
                    localWinMarks[bigIndex],
                    playedMark
            );
            BoardEffects.showGameOver(playedMark);
            return;
        }

        int next = controller.getNextForcedBoard();
        if (next == -1 || controller.isDeadBoard(next)) {
            BoardEffects.clearSmallHighlight();
            BoardEffects.freeMoveHighlight(playedMark);
        } else {
            BoardEffects.highlightBigCell(next, playedMark);
        }
    }
    
    /* ================= BOARD LOCK ================= */
    
    public static void updateInputLock(GameController controller) {
        boolean localTurn = controller.isLocalPlayersTurn();
        
        boardInputLayer.setMouseTransparent(!localTurn);
    }
    /* ================= HIGHLIGHT PANES ================= */
    
    private static Pane createHighlightPane() {
        Pane highlight = new Pane();

        highlight.setPrefSize(
                BIG_CELL_SIZE - HIGHLIGHT_INSET * 2,
                BIG_CELL_SIZE - HIGHLIGHT_INSET * 2
        );

        highlight.setVisible(false);
        highlight.setMouseTransparent(true);
        StackPane.setAlignment(highlight, Pos.CENTER);

        return highlight;
    }

    private static Pane createFreeMoveHighlight() {

        Pane p = new Pane();
        p.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        p.setMouseTransparent(true);
        p.setVisible(false);

        freeMoveHighlight = p;
        return p;
    }
    
    /* ================= LOCAL WIN MARK ================= */
    
    private static Text createLocalWinMark(int bigIndex, StackPane gameplayLayer) {

        Text localWinMark = new Text();
        localWinMark.setVisible(false);
        localWinMark.setMouseTransparent(true);

        localWinMark.setBoundsType(TextBoundsType.VISUAL);
        localWinMark.setTextOrigin(VPos.CENTER);
        StackPane.setAlignment(localWinMark, Pos.CENTER);

        gameplayLayers[bigIndex] = gameplayLayer;
        localWinMarks[bigIndex] = localWinMark;

        return localWinMark;
    }
    
    /* ================= GAME-OVER OVERLAY ================= */
    
    private static StackPane createGameOverOverlay() {

	    StackPane overlay = new StackPane();
	    overlay.setVisible(false);
	    overlay.setMouseTransparent(false);
	    overlay.setPickOnBounds(true);
	    overlay.setAlignment(Pos.CENTER);
	
	    Rectangle dim = new Rectangle(BOARD_SIZE, BOARD_SIZE);
	    dim.getStyleClass().add("game-over-dim");
	
	    gameOverTitle = new Text("Game Over.");
	    gameOverWinner = new Text();
	    gameOverWins = new Text("Wins!");
	
	    gameOverTitle.getStyleClass().add("game-over-title");
	    gameOverWinner.getStyleClass().add("game-over-winner");
	    gameOverWins.getStyleClass().add("game-over-wins");
	
	    for (Text t : new Text[]{gameOverTitle, gameOverWinner, gameOverWins}) {
	        t.setOpacity(0);                // animation-controlled
	        t.setMouseTransparent(true);
	        t.setTextOrigin(VPos.CENTER);
	    }
	
	    HBox winnerLine = new HBox(8, gameOverWinner, gameOverWins);
	    winnerLine.setAlignment(Pos.CENTER);
	
	    VBox content = new VBox(12, gameOverTitle, winnerLine);
	    content.setAlignment(Pos.CENTER);
	
	    overlay.getChildren().addAll(dim, content);
	
	    gameOverOverlay = overlay;
	    return overlay;
	}
    
    /* ================= HELPERS FOR createBoard ================= */
    
    private static StackPane createBoardRoot() {
        StackPane root = new StackPane();
        root.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        root.setMinSize(BOARD_SIZE, BOARD_SIZE);
        root.setMaxSize(BOARD_SIZE, BOARD_SIZE);
        return root;
    }

    private static GridPane createBoardGrid(GameController controller) {

        GridPane board = new GridPane();
        boardInputLayer = board;
        
        board.setPrefSize(BOARD_SIZE, BOARD_SIZE);

        for (int i = 0; i < 3; i++) {
            board.getColumnConstraints().add(new ColumnConstraints(BIG_CELL_SIZE));
            board.getRowConstraints().add(new RowConstraints(BIG_CELL_SIZE));
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int index = r * 3 + c;
                StackPane cell = createBigBoard(index, controller);
                bigCells[index] = cell;
                board.add(cell, c, r);
            }
        }

        return board;
    }

    private static StackPane createScaledWrapper(StackPane boardRoot) {

        StackPane wrapper = new StackPane(boardRoot);
        wrapper.setAlignment(Pos.CENTER);

        Scale boardScale = new Scale(1, 1);
        boardScale.setPivotX(BOARD_SIZE / 2);
        boardScale.setPivotY(BOARD_SIZE / 2);
        boardRoot.getTransforms().add(boardScale);

        wrapper.layoutBoundsProperty().addListener((obs, o, b) -> {
            double s = Math.min(
                    b.getWidth() / BOARD_SIZE,
                    b.getHeight() / BOARD_SIZE
            );
            boardScale.setX(s);
            boardScale.setY(s);
        });

        return wrapper;
    }


}
