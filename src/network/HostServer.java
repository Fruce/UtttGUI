package network;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import network.packetes.MovePacket;
import network.packetes.NewGameAcceptPacket;
import network.packetes.NewGameDenyPacket;
import network.packetes.NewGameOfferPacket;
import network.packetes.TakebackAcceptPacket;
import network.packetes.TakebackDenyPacket;
import network.packetes.TakebackOfferPacket;
import network.packetes.HandshakePacket;
import utils.UserSettings;

public class HostServer extends WebSocketServer implements NetworkEndpoint {

    private WebSocket client;
    private String opponentName;

    private final BlockingQueue<Object> inbox = new LinkedBlockingQueue<>();

    public HostServer(int port) {
        super(new InetSocketAddress("localhost", port));
    }

    /* ================= CONNECTION ================= */

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        client = conn;
        System.out.println("[HOST] Client connected!");
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer buffer) {
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);

        Object packet = PacketCodec.decode(data);
        inbox.offer(packet);
    }

    /* ================= HANDSHAKE ================= */

    public void startServer() throws Exception {
        start();
        System.out.println("[HOST] Waiting for client...");

        // wait for handshake
        Object packet = inbox.take();
        HandshakePacket clientHello = (HandshakePacket) packet;
        opponentName = clientHello.getUsername();

        // send host username
        HandshakePacket hostHello =
                new HandshakePacket(UserSettings.getUsername());
        sendPacket(hostHello);

        System.out.println("[HOST] Opponent username: " + opponentName);
    }

    public String getOpponentName() {
        return opponentName;
    }

    /* ================= GAME ================= */

    public Object receive() throws InterruptedException {
        return inbox.take(); // ANY packet
    }
    
    public void sendMove(int big, int small) {
        sendPacket(new MovePacket(big, small));
    }
    
    // NEW GAME
    
    @Override
    public void sendNewGameOffer() {
        sendPacket(new NewGameOfferPacket());
    }

    @Override
    public void sendNewGameAccept() {
        sendPacket(new NewGameAcceptPacket());
    }

    @Override
    public void sendNewGameDeny() {
        sendPacket(new NewGameDenyPacket());
    }
    
    // TAKEBACKS
    
    @Override
    public void sendTakebackOffer() {
        sendPacket(new TakebackOfferPacket());
    }

    @Override
    public void sendTakebackAccept() {
        sendPacket(new TakebackAcceptPacket());
    }

    @Override
    public void sendTakebackDeny() {
        sendPacket(new TakebackDenyPacket());
    }

    /* ================= UTIL ================= */

    private void sendPacket(Object packet) {
        byte[] encoded = PacketCodec.encode(packet);
        client.send(encoded);
    }
    
    public void close() {
        try {
            if (client != null && client.isOpen()) {
                client.close(1000, "Server shutting down");
            }
            this.stop();
            System.out.println("[HOST] Server closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean arg3) {
	    System.out.println("[HOST] Client disconnected: " + reason);
	    client = null;
		
	}

	@Override
	public void onError(WebSocket arg0, Exception ex) {
		ex.printStackTrace();
		
	}

	@Override
	public void onMessage(WebSocket arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		
	}

}

