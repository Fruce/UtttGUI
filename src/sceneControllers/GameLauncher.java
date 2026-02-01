package sceneControllers;

import java.io.IOException;

import game.GameController;
import game.GameMode;
import game.PlayerSource;
import javafx.application.Platform;
import main.SceneManager;
import network.ClientJoiner;
import network.HostServer;
import network.JoinCodeUtil;
import network.NetworkListener;
import network.NgrokAuthManager;
import network.NgrokManager;

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
	    	if (!NgrokAuthManager.hasToken()) {
	            Platform.runLater(() ->
	                SceneManager.switchTo("settings.fxml")
	            );
	            return;
	        }

	    NgrokManager.startTunnel(PORT);
	
	    HostServer host = new HostServer();
	
	    Platform.runLater(() -> {
	        HostWaitController waitUI =
	            SceneManager.switchToAndGetController("host-wait.fxml");
	
	        waitUI.setHostServer(host);
	        waitUI.showStarting();
	
	        NgrokManager.fetchPublicUrl(url -> {
	            String joinCode = JoinCodeUtil.encode(url);
	
	            Platform.runLater(() -> {
	                waitUI.setJoinCode(joinCode);
	            });
	        });
	    });
	
	    GameController controller =
	        new GameController(PlayerSource.LOCAL, PlayerSource.NETWORK);
	
	    controller.setHostServer(host);
	
	    new Thread(() -> {
	        try {
	            host.start(PORT); // blocks
	
	            Platform.runLater(() -> {
	                SceneManager.startGame(controller);
	            });
	
	            new NetworkListener(controller, host).start();
	
	        } catch (IOException e) {
	            System.out.println("[HOST] Host cancelled");
	        }
	    }, "host-server-thread").start();
	}



    

    /* ================= MULTIPLAYER JOIN ================= */

    public static void join(String joinCode) {
        try {
            String url = JoinCodeUtil.decode(joinCode);

            if (url == null) {
                throw new IllegalArgumentException("Invalid or expired join code");
            }

            // tcp://0.tcp.in.ngrok.io:17068

            String[] parts = url.split(":");

            String hostIp = parts[0];
            int port = Integer.parseInt(parts[1]);

            System.out.println("[JOIN] Connecting to " + hostIp + ":" + port);

            ClientJoiner client = new ClientJoiner();
            client.connect(hostIp, port);

            GameController controller =
                new GameController(PlayerSource.NETWORK, PlayerSource.LOCAL);

            controller.setClientJoiner(client);
            new NetworkListener(controller, client).start();

            Platform.runLater(() -> {
                SceneManager.startGame(controller);
            });

        } catch (Exception e) {
            throw new RuntimeException("Failed to join game", e);
        }
    }

}
