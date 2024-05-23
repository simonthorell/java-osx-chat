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
  private volatile boolean running = true;
  private ServerSocket serverSocket;

  // Constructor
  public ChatServer(int port) {
    this.port = port;
  }

  // Starts the chat server
  public void startServer() {
    try {
      // Create a server socket
      serverSocket = new ServerSocket(port);
      System.out.println("Server started on port: " + port);

      // Listen for incoming connections
      while (running) {
        try {
          // When a client connects a ClientHandler is added to the pool
          var clientSocket = serverSocket.accept();
          System.out.println("Client connected: " + clientSocket);
          var clientThread = new ClientHandler(clientSocket, this);
          clients.add(clientThread);
          pool.execute(clientThread);
        } catch (IOException e) {
          // If an error occurs while accepting a client connection
          System.out.println("Error accepting client connection");
        }
      }
    } catch (IOException e) {
      // If an error occurs while creating the server socket
      System.out.println("Could not listen on port: " + port);
    } finally {
      // When the server is stopped, close the server socket
      closeServerSocket();
      running = false;
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

  // Stops the server
  private void closeServerSocket() {
    try {
      if (serverSocket != null) {
        serverSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Could not close socket: " + e.getMessage());
    }
  }

  // Adds a user to the list of connected users
  public void addUser(String user) {
    synchronized (users) {
      users.add(user);
    }
  }

  // Removes a user from the list of connected users
  public void removeUser(String user) {
    synchronized (users) {
      users.remove(user);
    }
  }

  // Returns a copy of the connected users list to avoid concurrent modification
  public List<String> getUsers() {
    synchronized (users) {
      return new ArrayList<>(users);
    }
  }
}
