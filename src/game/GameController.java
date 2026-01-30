package game;

import move.MoveApplier;
import move.MoveSwitcher;
import move.MoveValidity;
import winConditions.GlobalWin;
import winConditions.LocalWin;

public class GameController {

    private char currentMark = 'O';
    private final GameState state = new GameState();
    private final int[] lastMove = { -1, -1 };
    private boolean gameOver = false;

    
    public GameController() {
        state.initialize();
    }

    /* ================= QUERY ================= */
    
    public MoveResult checkValidity(int big, int small) {
        return MoveValidity.isValid(
        			this,
                state.globalState,
                state.state,
                big,
                small,
                lastMove[0],
                lastMove[1]
        );
    }
    
    public boolean isDeadBoard(int small) {
 
    		if (state.globalState[small] != ' ') {
    			return true;
    		}

    		for (int i = 0; i < 9; i++) {
    			if (state.state[small][i] == ' ')
    				return false;
    			}
    		return true;
    	}
    

    public char getCurrentMark() {
        return currentMark;
    }

    public GameState getState() {
        return state;
    }
    
    public int getNextForcedBoard() {
        // -1 means "any board allowed"
        return lastMove[1];
    }
    
    public int[] getLastMove() {
    		return new int[] {lastMove[0], lastMove[0]};
    }

    /* ================= MOVE ================= */

    public MoveResult placeMove(int big, int small) {
    	
	    	if (gameOver) {
	            return MoveResult.INVALID;
	        }

        if (checkValidity(big, small) == MoveResult.INVALID) {
            return MoveResult.INVALID;
        }

        // apply move
        MoveApplier.updateMove(state.state, big, small, currentMark);

        boolean localWon = LocalWin.isLocalWon(state.state, big);
        boolean globalWon = false;

        if (localWon) {

            state.globalState[big] = currentMark;
            globalWon = GlobalWin.isGlobalWon(state.globalState);
        }

        // store last move
        lastMove[0] = big;
        lastMove[1] = small;

        currentMark = MoveSwitcher.switchMove(currentMark);

        if (globalWon) return MoveResult.GLOBAL_WIN;
        if (localWon) return MoveResult.LOCAL_WIN;
        return MoveResult.VALID;
    }
}
