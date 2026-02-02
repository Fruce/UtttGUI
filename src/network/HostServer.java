package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import network.packetes.MovePacket;
import network.packetes.HandshakePacket;
import utils.UserSettings;

public class HostServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private String opponentName;

    public void start(int port) throws IOException, ClassNotFoundException {
        serverSocket = new ServerSocket(port);
        System.out.println("[HOST] Waiting for client...");

        clientSocket = serverSocket.accept();
        System.out.println("[HOST] Client connected!");

        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        /* ================= HANDSHAKE ================= */

        // receive client username
        HandshakePacket clientHello =
                (HandshakePacket) in.readObject();
        opponentName = clientHello.getUsername();

        // send host username
        HandshakePacket hostHello =
                new HandshakePacket(UserSettings.getUsername());
        out.writeObject(hostHello);
        out.flush();

        System.out.println("[HOST] Opponent username: " + opponentName);
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
        clientSocket.close();
        serverSocket.close();
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[HOST] Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
