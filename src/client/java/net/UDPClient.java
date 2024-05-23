package client.java.net;

import client.java.*;
import common.ChatMessage;
import common.Constants;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class UDPClient implements IChatClient, Runnable {
  private final String username;
  private final List<String> users = new ArrayList<>();
  private final List<IUserListObserver> observers = new ArrayList<>();
  private MulticastSocket socket;
  private InetAddress group;
  private IMessageHandler handler;
  private volatile boolean running = true;

  public UDPClient(String username) {
    this.username = username;
  }

  @Override
  public void connect() throws IOException {
    socket = new MulticastSocket(Constants.MULTICAST_PORT);
    group = InetAddress.getByName(Constants.MULTICAST_GROUP_IP);
    socket.joinGroup(group);
    new Thread(this).start();

    // Broadcast a request for active users usernames
    sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_CONNECT));

    // Notify other clients that this user has entered the chat room
    sendMessage(new ChatMessage(username, "has joined the chat room!"));
    addUser(username);
  }

  @Override
  public void sendMessage(ChatMessage message) throws IOException {
    byte[] buf = serialize(message);
    DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Constants.MULTICAST_PORT);
    socket.send(packet);
  }

  @Override
  public void disconnect(IUserListObserver observer) throws IOException {
    // Notify other clients that this user has left the chat room
    sendMessage(new ChatMessage(username, "has left the chat room!"));
    sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_DISCONNECT));
    removeUser(username);

    // Wait for the thread to finish
    running = false;

    // Remove the observer
    removeUserListObserver(observer);

    // Close the socket
    if (socket != null) {
      socket.leaveGroup(group);
      socket.close();
    }
  }

  @Override
  public void setMessageHandler(IMessageHandler handler) {
    this.handler = handler;
  }

  @Override
  public void addUser(String username) {
    if (!users.contains(username)) {
      // Avoid duplicates option
      users.add(username);
      notifyUserListChanged();
    }
  }

  @Override
  public void removeUser(String username) {
    users.remove(username);
    notifyUserListChanged();
  }

  @Override
  public void addUserListObserver(IUserListObserver observer) {
    observers.add(observer);
  }

  @Override
  public void removeUserListObserver(IUserListObserver observer) {
    observers.remove(observer);
  }

  // Helper method to notify observers of changes to the user list
  private void notifyUserListChanged() {
    for (IUserListObserver observer : observers) {
      observer.userListUpdated(new ArrayList<>(users));
    }
  }

  @Override
  public void run() {
    try {
      while (running) {
        byte[] buf = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        ChatMessage message = deserialize(packet.getData());

        switch (message.getMessageType()) {
          case USER_CONNECT:
            // Respond with the current user's username to the group
            sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_RESPONSE));
            break;
          case USER_RESPONSE:
            addUser(message.getUser());
            break;
          case USER_DISCONNECT:
            removeUser(message.getUser());
            break;
          case CHAT_MESSAGE:
            if (handler != null) {
              handler.handleMessage(message);
            }
            break;
        }
      }
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Error receiving message: " + e.getMessage());
    }
  }

  // Helper method to serialize and deserialize ChatMessage objects
  private byte[] serialize(ChatMessage message) throws IOException {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos)) {
      oos.writeObject(message);
      return bos.toByteArray();
    }
  }

  // Helper method to serialize and deserialize ChatMessage objects
  private ChatMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis)) {
      return (ChatMessage) ois.readObject();
    }
  }
}
