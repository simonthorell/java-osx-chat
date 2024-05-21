package client.java.net;

import common.ChatMessage;
import client.java.IChatClient;
import common.Constants;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPClient implements IChatClient, Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private IMessageHandler handler;
    private final String username;
    private List<String> users = new ArrayList<>();
    private final List<IUserListObserver> observers = new ArrayList<>();

    //====================================================================================================
    // Constructors
    //====================================================================================================
    public TCPClient(String username) {
        this.username = username;
    }

    //====================================================================================================
    // Public Methods (IChatClient Interface)
    //====================================================================================================
    @Override
    public void connect() throws IOException {
        socket = new Socket(Constants.SERVER_TCP_IP, Constants.TCP_PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        new Thread(this).start();

        // Request connect users list
        sendMessage(new ChatMessage(username, ChatMessage.MessageType.USERNAME_REQUEST));
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    @Override
    public void disconnect(IUserListObserver observer) throws IOException {
        sendMessage(new ChatMessage(username, ChatMessage.MessageType.USER_DISCONNECT));
        socket.close();
        removeUserListObserver(observer);
    }

    @Override
    public void setMessageHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    //====================================================================================================
    // User Management (IChatClient Interface)
    //====================================================================================================
    @Override
    public void addUser(String username) {
        if (!users.contains(username)) {
            users.add(username);
            notifyUserListChanged();
        }
    }

    @Override
    public void removeUser(String username) {
        if (users.remove(username)) {
            notifyUserListChanged();
        }
    }

    @Override
    public void addUserListObserver(IUserListObserver observer) {
        observers.add(observer);
        observer.userListUpdated(new ArrayList<>(users));
    }

    @Override
    public void removeUserListObserver(IUserListObserver observer) {
        observers.remove(observer);
    }

    private void notifyUserListChanged() {
        for (IUserListObserver observer : observers) {
            observer.userListUpdated(new ArrayList<>(users));
        }
    }

    //====================================================================================================
    // Thread Methods (Runnable Interface)
    //====================================================================================================
    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                ChatMessage message = (ChatMessage) in.readObject();

                switch (message.getMessageType()) {
                    case USER_LIST: // This will include CONNECT & DISCONNECT messages
                        // Update the list of active users
                        users = message.getUsers();
                        System.out.println("Received user list: " + users);
                        // Notify observers of the updated user list
                        notifyUserListChanged();
                        break;
                    case CHAT_MESSAGE:
                        // TODO: Remove default case...
                    default:
                        // Handle all other messages (assumed to be chat messages)
                        if (handler != null) {
                            handler.handleMessage(message);
                        }
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling incoming message: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}