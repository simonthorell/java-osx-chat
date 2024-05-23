package server;

import common.ChatMessage;
import java.io.*;
import java.net.Socket;
import server.java.ChatServer;

public class ClientHandler implements Runnable {
  private final Socket socket;
  private final ObjectInputStream in;
  private final ObjectOutputStream out;
  private final ChatServer server;

  // Constructor
  public ClientHandler(Socket socket, ChatServer server) throws IOException {
    this.socket = socket;
    this.server = server;
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
  }

  // Run method (Runnable interface)
  @Override
  public void run() {
    try {
      ChatMessage inputMessage;
      while ((inputMessage = (ChatMessage) in.readObject()) != null) {

        // Handle message as defined in the ChatMessage class (common package)
        switch (inputMessage.getMessageType()) {
          case USER_CONNECT:
            server.addUser(inputMessage.getUser());
            server.broadcastMessage(
                new ChatMessage(ChatMessage.MessageType.USER_CONNECT, server.getUsers()));
            break;
          case USER_DISCONNECT:
            server.removeUser(inputMessage.getUser());
            server.broadcastMessage(
                new ChatMessage(ChatMessage.MessageType.USER_DISCONNECT, server.getUsers()));
            break;
          case CHAT_MESSAGE:
            System.out.println(inputMessage.getUser() + " sent: " + inputMessage.getMessage());
            server.broadcastMessage(inputMessage);
            break;
          default:
            System.out.println("Received unknown message type: " + inputMessage.getMessageType());
            break;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      // If an error occurs while reading the input stream
      System.out.println("Client disconnected");
    } finally {
      // Close resources and remove client from the server if disconnected
      closeResources();
      server.removeClient(this);
    }
  }

  // Sends a message to the client
  public void sendMessage(ChatMessage message) {
    System.out.println("Sent message: " + message);
    try {
      out.writeObject(message);
      out.flush();
    } catch (IOException e) {
      System.out.println("Error sending message: " + e.getMessage());
    }
  }

  // Closes all resources
  private void closeResources() {
    try {
      if (in != null) in.close();
      if (out != null) out.close();
      if (socket != null) socket.close();
    } catch (IOException e) {
      System.out.println("Error closing resources: " + e.getMessage());
    }
  }
}
