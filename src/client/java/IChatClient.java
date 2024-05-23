package client.java;

import client.java.net.IMessageHandler;
import client.java.net.IUserListObserver;
import common.ChatMessage;
import java.io.IOException;

public interface IChatClient {
  void connect() throws IOException;

  void sendMessage(ChatMessage message) throws IOException;

  void disconnect(IUserListObserver observer) throws IOException;

  void setMessageHandler(IMessageHandler handler);

  // User list methods
  void addUser(String username);

  void removeUser(String username);

  // Observer methods for user list
  void addUserListObserver(IUserListObserver observer);

  void removeUserListObserver(IUserListObserver observer);
}
