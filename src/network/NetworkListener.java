package network;

import board.BoardMaker;
import game.GameController;
import game.MoveResult;
import javafx.application.Platform;

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
                	
                	BoardMaker.refreshBoard(controller, move.big, move.small, result);
                	BoardMaker.updateInputLock(controller);
                	                	
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
