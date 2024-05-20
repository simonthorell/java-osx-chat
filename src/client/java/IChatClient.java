package client.java;

import java.io.IOException;

public interface IChatClient {
    void connect() throws IOException;
    void sendMessage(ChatMessage message) throws IOException;
    void disconnect() throws IOException;
    void setMessageHandler(IMessageHandler handler);
}
