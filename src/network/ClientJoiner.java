package network;

import java.io.*;
import java.net.Socket;

import network.packetes.MovePacket;
import network.packetes.HandshakePacket;
import utils.UserSettings;

public class ClientJoiner {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String opponentName;

    public void connect(String host, int port)
            throws IOException, ClassNotFoundException {

        socket = new Socket(host, port);
        System.out.println("[CLIENT] Connected to host!");

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        /* ================= HANDSHAKE ================= */

        // send client username
        HandshakePacket clientHello =
                new HandshakePacket(UserSettings.getUsername());
        out.writeObject(clientHello);
        out.flush();

        // receive host username
        HandshakePacket hostHello =
                (HandshakePacket) in.readObject();
        opponentName = hostHello.getUsername();

        System.out.println("[CLIENT] Opponent username: " + opponentName);
    }

    public String getOpponentName() {
        return opponentName;
    }

    /* ================= GAME ================= */

    public void sendMove(int big, int small) throws IOException {
        out.writeObject(new MovePacket(big, small));
        out.flush();
    }

    public MovePacket receiveMove() throws IOException, ClassNotFoundException {
        return (MovePacket) in.readObject();
    }

    /* ================= CLEANUP ================= */

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
