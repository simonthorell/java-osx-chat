package client.java.net;

import common.ChatMessage;

public interface IMessageHandler {
    void handleMessage(ChatMessage message);
}
