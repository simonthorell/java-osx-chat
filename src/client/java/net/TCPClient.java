package client.java.net;

import client.java.IChatClient;
import common.ChatMessage;
import common.Constants;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TCPClient implements IChatClient, Runnable {
  private final String username;
  private final List<IUserListObserver> observers = new ArrayList<>();
  private Socket socket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private IMessageHandler handler;
  private List<String> users = Collections.synchronizedList(new ArrayList<>());

  // Constructor
  public TCPClient(String username) {
    this.username = username;
  }

  // Connect to the server
  @Override
  public void connect() throws IOException {
    // Create a socket and start a new thread to handle incoming messages
    socket = new Socket(Constants.TCP_SERVER_IP, Constants.TCP_PORT);
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
    new Thread(this).start();

    // Request connected users list & notify that this user has entered the chat room
    sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_CONNECT));
    sendMessage(new ChatMessage(username, "has joined the chat room!"));
  }

  // Send a message to the server
  @Override
  public void sendMessage(ChatMessage message) throws IOException {
    out.writeObject(message);
    out.flush();
  }

  // Disconnect from the server
  @Override
  public void disconnect(IUserListObserver observer) throws IOException {
    // Notify other clients that this user has left the chat room
    sendMessage(new ChatMessage(username, "has left the chat room!"));
    sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_DISCONNECT));

    // Close the socket and remove the observer
    socket.close();
    removeUserListObserver(observer);
  }

  // Set the message handler
  @Override
  public void setMessageHandler(IMessageHandler handler) {
    this.handler = handler;
  }

  // Add or remove users
  @Override
  public void addUser(String username) {
    if (!users.contains(username)) {
      users.add(username);
      notifyUserListChanged();
    }
  }

  @Override
  public void removeUser(String username) {
    if (users.remove(username)) {
      notifyUserListChanged();
    }
  }

  // UI Observer methods
  @Override
  public void addUserListObserver(IUserListObserver observer) {
    observers.add(observer);
    observer.userListUpdated(new ArrayList<>(users));
  }

  @Override
  public void removeUserListObserver(IUserListObserver observer) {
    observers.remove(observer);
  }

  private void notifyUserListChanged() {
    for (IUserListObserver observer : observers) {
      observer.userListUpdated(new ArrayList<>(users));
    }
  }

  // Runnable interface method to handle incoming messages
  @Override
  public void run() {
    try {
      while (!socket.isClosed()) {
        ChatMessage message = (ChatMessage) in.readObject();

        switch (message.getMessageType()) {
          case USER_CONNECT, USER_DISCONNECT:
            users = message.getUsers(); // Update the list of active users
            System.out.println("Received user list: " + users);
            notifyUserListChanged(); // Notify observers
            break;
          case CHAT_MESSAGE:
            if (handler != null) {
              handler.handleMessage(message);
            }
            break;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Error handling incoming message: " + e.getMessage());
    } finally {
      try {
        in.close();
        out.close();
        socket.close();
      } catch (IOException e) {
        System.out.println("Error closing resources: " + e.getMessage());
      }
    }
  }
}
