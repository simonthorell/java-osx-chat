package client.java;

import java.io.IOException;
import java.util.List;

public interface IChatClient {
    void connect() throws IOException;
    void sendMessage(ChatMessage message) throws IOException;
    void disconnect() throws IOException;
    void setMessageHandler(IMessageHandler handler);

    // New methods to manage users
    List<String> getUsers(); // Get the list of user names
    void addUser(String username); // Add a user when they connect
    void removeUser(String username); // Remove a user when they disconnect
}
