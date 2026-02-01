package game;

import move.MoveApplier;
import move.MoveSwitcher;
import move.MoveValidity;
import network.ClientJoiner;
import network.HostServer;
import winConditions.GlobalWin;
import winConditions.LocalWin;

public class GameController {

    private char currentMark = 'O';
    private final GameState state = new GameState();
    private final int[] lastMove = { -1, -1 };
    private boolean gameOver = false;
    
    private HostServer hostServer;
    private ClientJoiner clientJoiner;
   
    
    private PlayerSource player1Source;
    private PlayerSource player2Source;

    private int currentPlayer = 1;
    
    public boolean firstMoveLocked;

    public GameController(PlayerSource p1, PlayerSource p2) {
        this.player1Source = p1;
        this.player2Source = p2;
        
        // Host always starts
        firstMoveLocked = (p1 == PlayerSource.NETWORK);
        state.initialize();
    }
    
    /* ================= QUERY ================= */
    private void sendNetworkMove(int big, int small) {
        try {
        	
            if (hostServer != null) {
                hostServer.sendMove(big, small);
            } else if (clientJoiner != null) {
                clientJoiner.sendMove(big, small);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setHostServer(HostServer hostServer) {
        this.hostServer = hostServer;
    }

    public void setClientJoiner(ClientJoiner clientJoiner) {
        this.clientJoiner = clientJoiner;
    }

    
    public boolean isLocalPlayersTurn() {
        if (currentPlayer == 1) {
            return player1Source == PlayerSource.LOCAL;
        } else {
            return player2Source == PlayerSource.LOCAL;
        }
    }
    
    public void switchTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    
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
    
//    public int[] getLastMove() {
//    		return new int[] {lastMove[0], lastMove[1]};
//    }

    /* ================= MOVE ================= */

    public MoveResult placeMove(int big, int small) {
    	
	    	if (firstMoveLocked) {
	            return MoveResult.INVALID;
	        }
    	
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
        switchTurn();
        
        // send move to network IF this move was made locally
        if (player1Source == PlayerSource.LOCAL && currentPlayer == 2) {
            sendNetworkMove(big, small);
        } else if (player2Source == PlayerSource.LOCAL && currentPlayer == 1) {
            sendNetworkMove(big, small);
        }


        if (globalWon) return MoveResult.GLOBAL_WIN;
        if (localWon) return MoveResult.LOCAL_WIN;
        return MoveResult.VALID;
    }
}
