package client.java.net;

import client.java.ChatMessage;

public interface IMessageHandler {
    void handleMessage(ChatMessage message);
}
