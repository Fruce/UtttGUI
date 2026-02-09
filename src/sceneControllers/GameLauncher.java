package sceneControllers;


import board.SidePane;
import game.GameController;
import game.GameMode;
import game.PlayerSource;
import javafx.application.Platform;
import main.SceneManager;

import network.ClientJoiner;
import network.CloudflareTunnel;
import network.HostServer;
import network.JoinCodeUtil;
import network.NetworkListener;


public class GameLauncher {

    private static final int PORT = 7777;

    /* ================= OFFLINE ================= */

    public static void launch(GameMode mode) {
        switch (mode) {

            case OFFLINE_2P -> {
                GameController controller =
                    new GameController(PlayerSource.LOCAL, PlayerSource.LOCAL);
                SceneManager.startGame(controller);
            }

            case OFFLINE_AI -> {
                GameController controller =
                    new GameController(PlayerSource.LOCAL, PlayerSource.AI);
                SceneManager.startGame(controller);
            }

            default -> throw new IllegalArgumentException(
                "Use host() or join() for multiplayer"
            );
        }
    }

    /* ================= MULTIPLAYER HOST ================= */
	public static void host() {

	    HostServer host = new HostServer(PORT);
	
	    final HostWaitController[] waitUIRef = new HostWaitController[1];
	
	    Platform.runLater(() -> {
	        waitUIRef[0] =
	            SceneManager.switchToAndGetController("host-wait.fxml");
	        waitUIRef[0].setHostServer(host);
	        waitUIRef[0].showStarting();
	    });
	
	    CloudflareTunnel.startTunnel(PORT, fullUrl -> {
	        String joinCode = JoinCodeUtil.extractCode(fullUrl);
	        System.out.println("JONCODE: " + joinCode);
	        Platform.runLater(() -> waitUIRef[0].setJoinCode(joinCode));
	    });
	
	    GameController controller =
	        new GameController(PlayerSource.LOCAL, PlayerSource.NETWORK);
	    controller.setHostServer(host);
	
	    new Thread(() -> {
	        try {
	            host.startServer(); // waits for handshake
	
	            String opponentName = host.getOpponentName();
	            controller.setOpponentName(opponentName);
	
	            // CREATE LISTENER FIRST
	            NetworkListener listener =
	                new NetworkListener(controller, host);
	            listener.start();
	
	            Platform.runLater(() -> {
	                SceneManager.startMultiplayerGame(
	                    controller,
	                    listener,
	                    host
	                );
	
	                SidePane.setOpponentName(opponentName);
	                SidePane.setActivePlayer(true);
	                SidePane.updateTurnText(true, controller.getCurrentMark());
	            });
	
	        } catch (Exception e) {
	            System.out.println("[HOST] Host cancelled");
	        }
	    }, "host-server-thread").start();
	}



  

    /* ================= MULTIPLAYER JOIN ================= */

	public static void join(String joinCode) {

	    String wsUrl = JoinCodeUtil.buildWebSocketUrl(joinCode);
	    if (wsUrl == null) {
	        throw new IllegalArgumentException("Invalid or expired join code");
	    }
	
	    ClientJoiner client = new ClientJoiner();
	
	    GameController controller =
	        new GameController(PlayerSource.NETWORK, PlayerSource.LOCAL);
	    controller.setClientJoiner(client);
	
	    new Thread(() -> {
	        try {
	            client.connect(wsUrl);
	            String opponentName = client.waitForOpponent();
	            controller.setOpponentName(opponentName);

	            NetworkListener listener =
	                new NetworkListener(controller, client);
	            listener.start();
	
	            Platform.runLater(() -> {
	                SceneManager.startMultiplayerGame(
	                    controller,
	                    listener,
	                    client
	                );
	
	                SidePane.setOpponentName(opponentName);
	                SidePane.setActivePlayer(false);
	                SidePane.updateTurnText(false, controller.getCurrentMark()
	                );
	            });
	
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }).start();
	}




}
