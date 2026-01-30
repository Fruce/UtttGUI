package board;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;

import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javafx.scene.text.Text;

import javafx.util.Duration;

public final class BoardAnimations {
	
	private static final Duration HIGHLIGHT_TIME = Duration.millis(180);

    /* ================= HOVER ================= */

    public static void hoverEnter(StackPane cell) {

        PauseTransition delay = new PauseTransition(Duration.millis(25));
        delay.setOnFinished(e -> {
            cell.getStyleClass().add("cell-hover");

            FadeTransition fadeIn = new FadeTransition(
                    Duration.millis(60), cell
            );
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        cell.getProperties().put("hoverEnter", delay);
        delay.play();
    }

    public static void hoverExit(StackPane cell) {

        PauseTransition enter =
                (PauseTransition) cell.getProperties().remove("hoverEnter");
        if (enter != null) enter.stop();

        if (!cell.getStyleClass().contains("cell-hover")) return;

        FadeTransition fadeOut = new FadeTransition(
                Duration.millis(200), cell
        );
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            cell.getStyleClass().remove("cell-hover");
            cell.setOpacity(1);
        });
        fadeOut.play();
    }

    /* ================= MARK PLACE ================= */

    public static void playMarkPlace(Text mark) {
	    	mark.setScaleX(1);
	    	mark.setScaleY(1);

        ScaleTransition pop = new ScaleTransition(
                Duration.millis(220), mark
        );

        pop.setFromX(0.6);
        pop.setFromY(0.6);

        pop.setToX(1.1);
        pop.setToY(1.1);

        pop.setInterpolator(Interpolator.EASE_OUT);

        pop.setOnFinished(e -> {
            ScaleTransition settle = new ScaleTransition(
                    Duration.millis(90), mark
            );
            settle.setToX(1.0);
            settle.setToY(1.0);
            settle.setInterpolator(Interpolator.EASE_IN);
            settle.play();
        });

        pop.play();
    }
    /* ================= SMALL HIGHLIGH ================= */
    
    public static void playHighlightEnter(Pane p) {

	    p.setOpacity(0);
	    p.setScaleX(1.1);
	    p.setScaleY(1.1);
	    p.setVisible(true);
	
	    FadeTransition fade = new FadeTransition(HIGHLIGHT_TIME, p);
	    fade.setFromValue(0);
	    fade.setToValue(1);
	
	    ScaleTransition scale = new ScaleTransition(HIGHLIGHT_TIME, p);
	    scale.setFromX(1.1);
	    scale.setToX(1);
	    scale.setFromY(1.1);
	    scale.setToY(1);
	
	    new ParallelTransition(fade, scale).play();
	}
    
    public static void playHighlightExit(Pane p) {
        if (!p.isVisible()) return;

        FadeTransition fade = new FadeTransition(HIGHLIGHT_TIME, p);
        fade.setFromValue(1);
        fade.setToValue(0);

        ScaleTransition scale = new ScaleTransition(HIGHLIGHT_TIME, p);
        scale.setFromX(1);
        scale.setToX(0.9);
        scale.setFromY(1);
        scale.setToY(0.9);

        ParallelTransition pt = new ParallelTransition(fade, scale);

        pt.setOnFinished(e -> {
            p.setVisible(false);
            p.setOpacity(1);
            p.setScaleX(1);
            p.setScaleY(1);
        });

        pt.play();
    }
    
    /* ================= FREE MOVE ================= */
    
    public static void playFreeMoveHighlight(Pane border) {

	    border.setVisible(true);
	    border.setOpacity(0);
	    border.setScaleX(0.95);
	    border.setScaleY(0.95);
	
	    // Fade + zoom in
	    FadeTransition fadeIn =
	            new FadeTransition(Duration.millis(200), border);
	    fadeIn.setFromValue(0);
	    fadeIn.setToValue(1);
	
	    ScaleTransition zoomIn =
	            new ScaleTransition(Duration.millis(200), border);
	    zoomIn.setFromX(0.95);
	    zoomIn.setFromY(0.95);
	    zoomIn.setToX(1.0);
	    zoomIn.setToY(1.0);
	
	    ParallelTransition appear =
	            new ParallelTransition(fadeIn, zoomIn);
	
	    // Small pulse (once)
	    ScaleTransition pulse =
	            new ScaleTransition(Duration.millis(80), border);
	    pulse.setFromX(1.0);
	    pulse.setFromY(1.0);
	    pulse.setToX(1.01);
	    pulse.setToY(1.01);
	    pulse.setAutoReverse(true);
	    pulse.setCycleCount(2); // up → back
	
	    // Play sequence
	    new SequentialTransition(appear, pulse).play();
}


    public static void stopFreeMoveHighlight(Pane border) {

        border.setVisible(false);
        border.setOpacity(1);
        border.setScaleX(1);
        border.setScaleY(1);
    }

    /* ================= SMALL GRID WIN ================= */

    public static void fadeOutSmallGrid(StackPane gridLines) {

        FadeTransition ft = new FadeTransition(
                Duration.millis(250), gridLines
        );
        ft.setToValue(0);
        ft.play();
    }
    
    public static void playLocalWinMark(Text mark) {

        // Reset state (important if reused)
        mark.setOpacity(0);
        mark.setScaleX(0.4);
        mark.setScaleY(0.4);

        FadeTransition fade = new FadeTransition(Duration.millis(250), mark);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(320), mark);
        scale.setFromX(0.4);
        scale.setFromY(0.4);
        scale.setToX(1.0);
        scale.setToY(1.0);

        // Slight overshoot feels good
        scale.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition popIn = new ParallelTransition(fade, scale);
        popIn.play();
        
        ScaleTransition pulse = new ScaleTransition(Duration.millis(100), mark);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);

        popIn.setOnFinished(e -> pulse.play());
    }
    
    //================ GAME-OVER OVERLAY ==================
    
    public static void playGameOver() {

	    Text title  = BoardMaker.getGameOverTitle();   // "Game Over."
	    Text winner = BoardMaker.getGameOverWinner();  // "X"
	    Text wins   = BoardMaker.getGameOverWins();    // "Wins!"
	
	    /* ---------- Reset ---------- */
	    title.setOpacity(0);
	    title.setScaleX(0.6);
	    title.setScaleY(0.6);
	
	    winner.setOpacity(0);
	    wins.setOpacity(0);
	
	    /* ======================================================
	       GAME OVER.  (fade + pop + pulse)
	       ====================================================== */
	
	    FadeTransition titleFade =
	            new FadeTransition(Duration.millis(320), title);
	    titleFade.setFromValue(0);
	    titleFade.setToValue(1);
	
	    ScaleTransition titlePop =
	            new ScaleTransition(Duration.millis(300), title);
	    titlePop.setFromX(0.6);
	    titlePop.setFromY(0.6);
	    titlePop.setToX(1.0);
	    titlePop.setToY(1.0);
	    titlePop.setInterpolator(Interpolator.EASE_OUT);
	
	    ParallelTransition titleIn =
	            new ParallelTransition(titleFade, titlePop);
	
	    ScaleTransition titlePulse =
	            new ScaleTransition(Duration.millis(180), title);
	    titlePulse.setFromX(1.0);
	    titlePulse.setFromY(1.0);
	    titlePulse.setToX(1.06);
	    titlePulse.setToY(1.06);
	    titlePulse.setAutoReverse(true);
	    titlePulse.setCycleCount(2);
	
	    SequentialTransition titleAnim =
	            new SequentialTransition(titleIn, titlePulse);
	
	    /* ======================================================
	    X and Wins! (overlapping fades)
	    ====================================================== */
		
		 // X fades in
		 FadeTransition winnerFade =
		         new FadeTransition(Duration.millis(550), winner);
		 winnerFade.setFromValue(0);
		 winnerFade.setToValue(1);
		
		 // Wins fades in
		 FadeTransition winsFade =
		         new FadeTransition(Duration.millis(450), wins);
		 winsFade.setFromValue(0);
		 winsFade.setToValue(1);
		
		 // Delay ONLY the start of "Wins!"
		 PauseTransition winsDelay =
		         new PauseTransition(Duration.millis(175));
		
		 // Wins = delay → fade
		 SequentialTransition delayedWinsFade =
		         new SequentialTransition(
		                 winsDelay,
		                 winsFade
		         );
		
		 // Play X and delayed Wins together
		 ParallelTransition winnerLineAnim =
		         new ParallelTransition(
		                 winnerFade,
		                 delayedWinsFade
		         );
	
	
	    /* ======================================================
	       Start winner line a bit later
	       ====================================================== */
	
	    PauseTransition delayBeforeWinner =
	            new PauseTransition(Duration.millis(400));  
	
	    SequentialTransition delayedWinnerAnim =
	            new SequentialTransition(
	                    delayBeforeWinner,
	                    winnerLineAnim
	            );
	
	    /* ======================================================
	       PLAY TOGETHER
	       ====================================================== */
	
	    ParallelTransition all =
	            new ParallelTransition(
	                    titleAnim,
	                    delayedWinnerAnim
	            );
	
	    all.play();
	}


    public static void playGameOverBlur() {

        BoxBlur blur = BoardEffects.getBlurEffect();

        Timeline blurIn = new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new KeyValue(blur.widthProperty(), 0),
                new KeyValue(blur.heightProperty(), 0)
            ),
            new KeyFrame(
                Duration.millis(500),   // slow, smooth
                new KeyValue(blur.widthProperty(), 5),
                new KeyValue(blur.heightProperty(), 5)
            )
        );

        blurIn.play();
    }


}
