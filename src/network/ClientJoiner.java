package network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
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

public class ClientJoiner implements NetworkEndpoint {

    private WebSocketClient client;
    private final BlockingQueue<Object> inbox = new LinkedBlockingQueue<>();
    private String opponentName;

    /* ================= CONNECT ================= */

    public void connect(String inviteLink) throws Exception {
        String wsUrl = inviteLink.replace("https://", "wss://");

        client = new WebSocketClient(new URI(wsUrl)) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("[CLIENT] Connected to host!");

                HandshakePacket hello =
                        new HandshakePacket(UserSettings.getUsername());
                send(PacketCodec.encode(hello));
            }

            @Override
            public void onMessage(ByteBuffer buffer) {
                byte[] data = new byte[buffer.remaining()];
                buffer.get(data);
                inbox.offer(PacketCodec.decode(data));
            }

            @Override
            public void onMessage(String message) {
                // unused (binary protocol only)
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("[CLIENT] Disconnected: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        // ONLY wait for socket open
        client.connectBlocking();
    }

    /* ================= HANDSHAKE ================= */

    public String waitForOpponent() throws InterruptedException {
        Object packet = inbox.take();
        HandshakePacket hostHello = (HandshakePacket) packet;
        opponentName = hostHello.getUsername();
        return opponentName;
    }

    public String getOpponentName() {
        return opponentName;
    }

    /* ================= GAME ================= */
    
    public Object receive() throws InterruptedException {
        return inbox.take(); // ANY packet
    }
    
    @Override
    public void sendMove(int big, int small) {
        client.send(PacketCodec.encode(new MovePacket(big, small)));
    }
    
    // NEW GAME 
    
    @Override
    public void sendNewGameOffer() {
    		client.send(PacketCodec.encode(new NewGameOfferPacket()));
    }

    @Override
    public void sendNewGameAccept() {
    		client.send(PacketCodec.encode(new NewGameAcceptPacket()));
    }

    @Override
    public void sendNewGameDeny() {
    		client.send(PacketCodec.encode(new NewGameDenyPacket()));
    }
    
    // TAKEBACK
    
    @Override
    public void sendTakebackOffer() {
    		client.send(PacketCodec.encode(new TakebackOfferPacket()));
    }

    @Override
    public void sendTakebackAccept() {
    		client.send(PacketCodec.encode(new TakebackAcceptPacket()));
    }

    @Override
    public void sendTakebackDeny() {
    		client.send(PacketCodec.encode(new TakebackDenyPacket()));
    }

    /* ================= CLEANUP ================= */

    @Override
    public void close() {
        if (client != null && client.isOpen()) {
            client.close(1000, "Client leaving");
        }
    }
}
