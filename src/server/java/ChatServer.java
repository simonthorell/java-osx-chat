package server.java;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;
import java.util.ArrayList;

public class ChatServer {
    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final List<ClientHandler> clients = new ArrayList<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port: " + port);
            while (true) {
                var clientSocket = serverSocket.accept();
                var clientThread = new ClientHandler(clientSocket, this);
                clients.add(clientThread);
                pool.execute(clientThread);
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + port);
        } finally {
            closeServerSocket();
        }
    }

    void broadcastMessage(String message, ClientHandler excludeUser) {
        for (ClientHandler aClient : clients) {
            if (aClient != excludeUser) {
                aClient.sendMessage(message);
            }
        }
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    private void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Could not close server socket: " + e.getMessage());
        }
    }
}
