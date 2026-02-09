package network;

import board.BoardMaker;
import board.SidePane;
import game.GameController;
import game.MoveResult;
import javafx.application.Platform;
import move.MoveSwitcher;
import network.packetes.MovePacket;
import network.packetes.NewGameAcceptPacket;
import network.packetes.NewGameDenyPacket;
import network.packetes.NewGameOfferPacket;
import network.packetes.TakebackAcceptPacket;
import network.packetes.TakebackDenyPacket;
import network.packetes.TakebackOfferPacket;

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

                Object packet;

                if (network instanceof HostServer hs) {
                    packet = hs.receive();
                } else {
                    packet = ((ClientJoiner) network).receive();
                }

                Object finalPacket = packet;

                Platform.runLater(() -> {

                    // ===== MOVE =====
                    if (finalPacket instanceof MovePacket move) {

                        controller.firstMoveLocked = false;

                        MoveResult result =
                                controller.placeMove(move.big, move.small);

                        boolean isLocal =
                                controller.isLocalPlayersTurn();

                        char oppMark =
                                MoveSwitcher.switchMove(
                                        controller.getCurrentMark()
                                );

                        BoardMaker.refreshBoard(
                                controller,
                                move.big,
                                move.small,
                                result,
                                oppMark
                        );

                        BoardMaker.updateInputLock(controller);
                        SidePane.setActivePlayer(isLocal);

                        if (isLocal) {
                            SidePane.updateTurnText(
                                    true,
                                    controller.getCurrentMark()
                            );
                        }

                    }

                    // ===== NEW GAME OFFER =====
                    else if (finalPacket instanceof NewGameOfferPacket) {
                        SidePane.showNewGameOfferReceived();
                    }

                    // ===== NEW GAME ACCEPT =====
                    else if (finalPacket instanceof NewGameAcceptPacket) {
                        controller.receiveNewGameAccepted();
                    }

                    // ===== NEW GAME DENY =====
                    else if (finalPacket instanceof NewGameDenyPacket) {
                        controller.receiveNewGameDenied();
                    }

                    // ===== TAKEBACK (same idea) =====
                    else if (finalPacket instanceof TakebackOfferPacket) {
                        SidePane.showTakebackOfferReceived();
                    }

                    else if (finalPacket instanceof TakebackAcceptPacket) {
                        controller.receiveTakebackAccepted();
                    }

                    else if (finalPacket instanceof TakebackDenyPacket) {
                        controller.receiveTakebackDenied();
                    }

                    else {
                        System.err.println(
                            "Unknown packet type: " +
                            finalPacket.getClass()
                        );
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
