package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatMessage implements Serializable {
    public enum MessageType {
        USERNAME_REQUEST, USER_RESPONSE, USER_LIST, USER_DISCONNECT, CHAT_MESSAGE
    }
    private final String user;
    private final String message;
    private final MessageType msgType;
    private final String timestamp;
    private final List<String> users;

    // Constructor for chat messages
    public ChatMessage(String user, String message) {
        this.user = user;
        this.message = message;
        this.msgType = MessageType.CHAT_MESSAGE; // Chat message
        this.timestamp = setTimestamp();
        this.users = null;
    }

    // Constructor for silent settings messages
    public ChatMessage(String user, MessageType msgType) {
        this.user = user;
        this.message = "";
        this.msgType = msgType;
        this.timestamp = setTimestamp();
        this.users = null;
    }

    // Send user list
    public ChatMessage(String user, MessageType msgType, List<String> users) {
        this.user = user;
        this.message = "";
        this.msgType = msgType;
        this.timestamp = setTimestamp();
        this.users = users;
    }

    public String setTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public MessageType getMessageType() {
        return msgType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<String> getUsers() {
        return users;
    }
}
