package server.java;

import common.ChatMessage;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server.ClientHandler;

public class ChatServer {
  private final int port;
  private final ExecutorService pool = Executors.newCachedThreadPool();
  private final List<ClientHandler> clients = new ArrayList<>();
  private final List<String> users = new ArrayList<>();
  private ServerSocket serverSocket;

  public ChatServer(int port) {
    this.port = port;
  }

  public void startServer() {
    try {
      serverSocket = new ServerSocket(port);
      System.out.println("Server started on port: " + port);
      while (true) {
        // When a client connects a ClientHandler is added to the pool
        var clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket);
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

  // Sends a message to all connected clients
  public void broadcastMessage(ChatMessage message) {
    for (ClientHandler client : clients) {
      System.out.println("Broadcast message to: " + client);
      client.sendMessage(message);
      System.out.println(message.getUsers());
    }
  }

  // Removes a client from the list of connected clients
  public void removeClient(ClientHandler client) {
    clients.remove(client);
  }

  private void closeServerSocket() {
    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Could not close socket: " + e.getMessage());
    }
  }

  public void addUser(String user) {
    synchronized (users) {
      users.add(user);
    }
  }

  public void removeUser(String user) {
    synchronized (users) {
      users.remove(user);
    }
  }

  public List<String> getUsers() {
    synchronized (users) {
      // Return a copy to avoid issues while iterating outside
      // the synchronized block
      return new ArrayList<>(users);
    }
  }
}
