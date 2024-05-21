package client.java;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatMessage implements Serializable {
    private final String user;
    private final String message;
    private final String timestamp;  // Change to String

    // Constructor for messages without user list
    public ChatMessage(String user, String message) {
        this.user = user;
        this.message = message;
        this.timestamp = setTimestamp();
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

    public String getTimestamp() {
        return timestamp;
    }
}
