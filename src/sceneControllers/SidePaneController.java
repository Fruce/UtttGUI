package sceneControllers;

import board.BoardMaker;
import board.SidePane;
import game.GameController;
import game.PlayerSource;
import main.SceneManager;
import network.NetworkEndpoint;
import network.NetworkListener;

public final class SidePaneController {

    private GameController controller;
    private NetworkListener listener;
    private final NetworkEndpoint network;

    public SidePaneController(
            GameController controller,
            NetworkListener listener,
            NetworkEndpoint network
    ) {
        this.controller = controller;
        this.listener = listener;
        this.network = network;

        /* ---------- OFFER BUTTONS ---------- */

        SidePane.getNewGameButton()
                .setOnAction(e -> sendNewGameOffer());

        SidePane.getTakebackButton()
                .setOnAction(e -> sendTakebackOffer());

        /* ---------- NEW GAME ACCEPT / DENY ---------- */

        SidePane.getNewGameAccept()
                .setOnMouseClicked(e -> acceptNewGame());

        SidePane.getNewGameDeny()
                .setOnMouseClicked(e -> denyNewGame());

        /* ---------- TAKEBACK ACCEPT / DENY ---------- */

        SidePane.getTakebackAccept()
                .setOnMouseClicked(e -> acceptTakeback());

        SidePane.getTakebackDeny()
                .setOnMouseClicked(e -> denyTakeback());
    }

    /* ======================
       CALLBACKS (NEW GAME)
       ====================== */

    public void onNewGameOfferReceived() {
        SidePane.showNewGameOfferReceived();
    }

    public void onNewGameAccepted() {
        restartGame(true);
    }

    public void onNewGameDenied() {
        SidePane.showNormalControls();
    }

    /* ======================
       CALLBACKS (TAKEBACK)
       ====================== */

    public void onTakebackOfferReceived() {
        SidePane.showTakebackOfferReceived();
    }

    public void onTakebackAccepted() {
        undoAndRefresh();
        SidePane.showTakebackNormalControls();
    }

    public void onTakebackDenied() {
        SidePane.showTakebackNormalControls();
    }

    /* ======================
       BUTTON ACTIONS
       ====================== */

    private void sendNewGameOffer() {
        SidePane.showNewGameOfferSent();
        controller.sendNewGameOffer();
    }

    private void acceptNewGame() {
        controller.sendNewGameAccept();
        restartGame(false);
    }

    private void denyNewGame() {
        controller.sendNewGameDeny();
        SidePane.showNormalControls();
    }

    private void sendTakebackOffer() {
        SidePane.showTakebackOfferSent();
        controller.sendTakebackOffer();
    }

    private void acceptTakeback() {
        undoAndRefresh();
        controller.sendTakebackAccept();
        SidePane.showTakebackNormalControls();
    }

    private void denyTakeback() {
        controller.sendTakebackDeny();
        SidePane.showTakebackNormalControls();
    }

    /* ======================
       HELPERS
       ====================== */

    private void undoAndRefresh() {
    		controller.undoLastMove();
        BoardMaker.fullRedraw(controller);
        BoardMaker.updateInputLock(controller);

        SidePane.setActivePlayer(controller.isLocalPlayersTurn());
        SidePane.updateTurnText(
                controller.isLocalPlayersTurn(),
                controller.getCurrentMark()
        );
    }

    private void restartGame(boolean localStarts) {

        if (listener != null) {
            listener.interrupt();
            listener = null;
        }

        String opponentName = controller.getOpponentName();

        GameController newController =
                localStarts
                        ? new GameController(PlayerSource.LOCAL, PlayerSource.NETWORK)
                        : new GameController(PlayerSource.NETWORK, PlayerSource.LOCAL);

        if (network instanceof network.HostServer host) {
            newController.setHostServer(host);
        } else if (network instanceof network.ClientJoiner client) {
            newController.setClientJoiner(client);
        }

        newController.setOpponentName(opponentName);

        NetworkListener newListener =
                new NetworkListener(newController, network);
        newListener.start();

        SceneManager.startMultiplayerGame(
                newController,
                newListener,
                network
        );

        this.controller = newController;
        this.listener = newListener;

        SidePane.showNormalControls();
        SidePane.showTakebackNormalControls();
        SidePane.setActivePlayer(localStarts);
        SidePane.setOpponentName(opponentName);
        SidePane.updateTurnText(localStarts, 'X');
    }
}
