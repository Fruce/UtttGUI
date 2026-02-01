package network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HostServer {

    private ServerSocket serverSocket;
    private Socket clientSocket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Waiting for client...");

        clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
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
        clientSocket.close();
        serverSocket.close();
    }
    
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("[HOST] Server stopped");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

