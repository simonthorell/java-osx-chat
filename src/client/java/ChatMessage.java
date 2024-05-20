package client.java;

import java.io.Serializable;

public class ChatMessage implements Serializable {
    private String user;
    private String message;
    private String timestamp;  // Change to String

    public ChatMessage(String user, String message, String timestamp) {
        this.user = user;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}
