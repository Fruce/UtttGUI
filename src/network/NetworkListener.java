package network;

import board.BoardMaker;
import board.SidePane;
import game.GameController;
import game.MoveResult;
import javafx.application.Platform;
import move.MoveSwitcher;
import network.packetes.MovePacket;

public class NetworkListener extends Thread {

    private final GameController controller;
    private final Object network;

    public NetworkListener(GameController controller, Object network) {
        this.controller = controller;
        this.network = network;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                MovePacket move;

                if (network instanceof HostServer) {
                    move = ((HostServer) network).receiveMove();
                } else {
                    move = ((ClientJoiner) network).receiveMove();
                }

                Platform.runLater(() -> {
                	
                	controller.firstMoveLocked = false;
                	MoveResult result = controller.placeMove(move.big, move.small);
                	
                	boolean isLocal = controller.isLocalPlayersTurn();
                	
                	char oppMark = MoveSwitcher.switchMove(controller.getCurrentMark());
                	
                	BoardMaker.refreshBoard(controller, move.big, move.small, result);
                	BoardMaker.updateInputLock(controller);
                	SidePane.setActivePlayer(isLocal);
                	
                    if (isLocal) {
                        SidePane.updateTurnText(true, oppMark); }
  
                	                	
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
