package network;

import java.io.*;
import java.net.Socket;

public class ClientJoiner {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        System.out.println("Connected to host!");

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void sendMove(int big, int small) throws IOException {
        out.writeObject(new MovePacket(big, small));
        out.flush();
    }

    public MovePacket receiveMove() throws IOException, ClassNotFoundException {
        return (MovePacket) in.readObject();
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
