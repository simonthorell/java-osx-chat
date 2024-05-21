package server;

import server.java.ChatServer;
import common.ChatMessage;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final ChatServer server;

    public ClientHandler(Socket socket, ChatServer server) throws IOException {
        this.socket = socket;
        this.server = server;
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            ChatMessage inputMessage;
            while ((inputMessage = (ChatMessage) in.readObject()) != null) {

                switch(inputMessage.getMessageType()) {
                    case USERNAME_REQUEST:
                        System.out.println(inputMessage.getUser() + " requested user list");
                        // This is new user, so add it to user list
                        server.users.add(inputMessage.getUser());
                        System.out.println(server.users);
                        server.broadcastMessage(new ChatMessage(inputMessage.getUser(),
                                ChatMessage.MessageType.USER_LIST, server.users));
                        break;
                    case USER_RESPONSE:
                        // TODO: Remove for TCP... Handle with server user DB.
                        break;
                    case USER_DISCONNECT:
                        // Remove user from user list
                        server.users.remove(inputMessage.getUser());
                        break;
                    case CHAT_MESSAGE:
                        // Broadcast message to all clients
                        System.out.println(inputMessage.getUser() + " sent: " +
                                inputMessage.getMessage());
                        server.broadcastMessage(inputMessage);
                        break;
                    default:
                        System.out.println("Received unknown message type: " +
                                inputMessage.getMessageType());
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected");
        } finally {
            closeResources();
            server.removeClient(this);
        }
    }

    public void sendMessage(ChatMessage message) {
        System.out.println("Sent message: " + message);
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}