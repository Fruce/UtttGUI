package game;

import move.MoveApplier;
import move.MoveSwitcher;
import move.MoveValidity;
import move.MoveRecord;
import network.ClientJoiner;
import network.HostServer;
import winConditions.GlobalWin;
import winConditions.LocalWin;
import java.util.Stack;

public class GameController {

    /* ================= GAME STATE ================= */

    private char currentMark = 'X';
    private GameState state = new GameState();
    private final int[] lastMove = { -1, -1 };
    private boolean gameOver = false;

    private int currentPlayer = 1;
    public boolean firstMoveLocked;

    public PlayerSource player1Source;
    public PlayerSource player2Source;

    /* ================= NETWORK ================= */

    private HostServer hostServer;
    private ClientJoiner clientJoiner;

    /* ================= NEW GAME OFFER ================= */

    private boolean newGameOfferPending = false;

    // Callbacks (UI registers here)
    private Runnable onNewGameOfferReceived;
    private Runnable onNewGameAccepted;
    private Runnable onNewGameDenied;
    
    /* ================= TAKEBACK OFFER ================= */
    
    private final Stack<MoveRecord> moveHistory = new Stack<>();
    private boolean takebackOfferPending = false;
    
    private Runnable onTakebackOfferReceived;
    private Runnable onTakebackAccepted;
    private Runnable onTakebackDenied;

    /* ================= CONSTRUCTOR ================= */

    public GameController(PlayerSource p1, PlayerSource p2) {
        this.player1Source = p1;
        this.player2Source = p2;

        // Host always starts
        firstMoveLocked = (p1 == PlayerSource.NETWORK);
        
        //firstMoveLocked = (currentMark != 'X');
        state.initialize();
    }

    /* ================= NETWORK SETUP ================= */

    public void setHostServer(HostServer hostServer) {
        this.hostServer = hostServer;
    }

    public void setClientJoiner(ClientJoiner clientJoiner) {
        this.clientJoiner = clientJoiner;
    }

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

    /* ================= TURN HELPERS ================= */

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

    public char getCurrentMark() {
        return currentMark;
    }

    public GameState getState() {
        return state;
    }

    public int getNextForcedBoard() {
        return lastMove[1]; // -1 means any board allowed
    }
    
    /* ================= UNDO LAST MOVE ================= */
    
    public void undoLastMove() {
        if (moveHistory.isEmpty()) return;

        MoveRecord last = moveHistory.pop();

        // Restore board cell
        state.state[last.big][last.small] = ' ';

        // Restore local board state
        state.globalState[last.big] = last.prevGlobalState;

        // Restore last-move restriction
        lastMove[0] = last.prevLastBig;
        lastMove[1] = last.prevLastSmall;

        // Restore turn + mark
        currentMark = last.previousMark;
        currentPlayer = last.previousPlayer;

        // Restore game state
        gameOver = last.prevGameOver;
        
        return;
    }


    /* ================= VALIDATION ================= */

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
        if (state.globalState[small] != ' ') return true;

        for (int i = 0; i < 9; i++) {
            if (state.state[small][i] == ' ') return false;
        }
        return true;
    }

    /* ================= MOVE ================= */

    public MoveResult placeMove(int big, int small) {

        if (firstMoveLocked || gameOver) {
            return MoveResult.INVALID;
        }

        if (checkValidity(big, small) == MoveResult.INVALID) {
            return MoveResult.INVALID;
        }
        
        // Record move
        moveHistory.push(new MoveRecord(
        	    big,
        	    small,
        	    currentMark,
        	    currentPlayer,
        	    lastMove[0],
        	    lastMove[1],
        	    state.globalState[big],
        	    gameOver
        	));
        
        // Apply move
        MoveApplier.updateMove(state.state, big, small, currentMark);

        boolean localWon = LocalWin.isLocalWon(state.state, big);
        boolean globalWon = false;

        if (localWon) {
            state.globalState[big] = currentMark;
            globalWon = GlobalWin.isGlobalWon(state.globalState);
        }

        // Store last move
        lastMove[0] = big;
        lastMove[1] = small;

        currentMark = MoveSwitcher.switchMove(currentMark);
        switchTurn();

        // Send move if made locally
        if (player1Source == PlayerSource.LOCAL && currentPlayer == 2) {
            sendNetworkMove(big, small);
        } else if (player2Source == PlayerSource.LOCAL && currentPlayer == 1) {
            sendNetworkMove(big, small);
        }

        if (globalWon) {
            gameOver = true;
            return MoveResult.GLOBAL_WIN;
        }

        if (localWon) return MoveResult.LOCAL_WIN;
        return MoveResult.VALID;
    }

    /* ================= NEW GAME OFFER : OUTBOUND ================= */

    public void sendNewGameOffer() {
        if (newGameOfferPending) return;
        newGameOfferPending = true;

        try {
            if (hostServer != null) {
                hostServer.sendNewGameOffer();
            } else if (clientJoiner != null) {
                clientJoiner.sendNewGameOffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewGameAccept() {
        newGameOfferPending = false;

        try {
            if (hostServer != null) {
                hostServer.sendNewGameAccept();
            } else if (clientJoiner != null) {
                clientJoiner.sendNewGameAccept();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendNewGameDeny() {
        newGameOfferPending = false;

        try {
            if (hostServer != null) {
                hostServer.sendNewGameDeny();
            } else if (clientJoiner != null) {
                clientJoiner.sendNewGameDeny();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /* ================= TAKEBACK OFFER : OUTBOUND ================= */
    
    public void sendTakebackOffer() {
        if (takebackOfferPending) return;
        takebackOfferPending = true;

        try {
            if (hostServer != null) {
                hostServer.sendTakebackOffer();
            } else if (clientJoiner != null) {
                clientJoiner.sendTakebackOffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTakebackAccept() {
        takebackOfferPending = false;

        try {
            if (hostServer != null) {
                hostServer.sendTakebackAccept();
            } else if (clientJoiner != null) {
                clientJoiner.sendTakebackAccept();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendTakebackDeny() {
        takebackOfferPending = false;

        try {
            if (hostServer != null) {
                hostServer.sendTakebackDeny();
            } else if (clientJoiner != null) {
                clientJoiner.sendTakebackDeny();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= NEW GAME OFFER : INBOUND ================= */

    public void receiveNewGameOffer() {
        newGameOfferPending = true;
        if (onNewGameOfferReceived != null) {
            onNewGameOfferReceived.run();
        }
    }

    public void receiveNewGameAccepted() {
        newGameOfferPending = false;
        if (onNewGameAccepted != null) {
            onNewGameAccepted.run();
        }
    }

    public void receiveNewGameDenied() {
        newGameOfferPending = false;
        if (onNewGameDenied != null) {
            onNewGameDenied.run();
        }
    }
    
    /* ================= TAKEBACK OFFER : INBOUND ================= */
    
    public void receiveTakebackOffer() {
        takebackOfferPending = true;
        if (onTakebackOfferReceived != null) {
            onTakebackOfferReceived.run();
        }
    }

    public void receiveTakebackAccepted() {
        takebackOfferPending = false;
        if (onTakebackAccepted != null) {
            onTakebackAccepted.run();
        }
    }

    public void receiveTakebackDenied() {
        takebackOfferPending = false;
        if (onTakebackDenied != null) {
            onTakebackDenied.run();
        }
    }

    /* ================= CALLBACK REGISTRATION ================= */

    public void setOnNewGameOfferReceived(Runnable r) {
        this.onNewGameOfferReceived = r;
    }

    public void setOnNewGameAccepted(Runnable r) {
        this.onNewGameAccepted = r;
    }

    public void setOnNewGameDenied(Runnable r) {
        this.onNewGameDenied = r;
    }
    
    public void setOnTakebackOfferReceived(Runnable r) {
        this.onTakebackOfferReceived = r;
    }

    public void setOnTakebackAccepted(Runnable r) {
        this.onTakebackAccepted = r;
    }

    public void setOnTakebackDenied(Runnable r) {
        this.onTakebackDenied = r;
    }
    
    /* ================= STORING OPPONENT NAME ================= */
    
    private String opponentName;

    public void setOpponentName(String name) {
        this.opponentName = name;
    }

    public String getOpponentName() {
        return opponentName;
    }

}
