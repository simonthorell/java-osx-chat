package client.java.net;

import client.java.ChatMessage;
import client.java.IChatClient;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPClient implements IChatClient, Runnable{
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private IMessageHandler handler;
    private final List<String> users = new ArrayList<>();
    private final List<IUserListObserver> observers = new ArrayList<>();

    //====================================================================================================
    // Constructors
    //====================================================================================================
    public TCPClient(String username) {
        users.add(username);
    }

    //====================================================================================================
    // Public Methods (IChatClient Interface)
    //====================================================================================================
    @Override
    public void connect() throws IOException {
        socket = new Socket("serverAddress", Constants.TCP_PORT); // Replace with actual address and port
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        new Thread(this).start();
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    @Override
    public void disconnect(IUserListObserver observer) throws IOException {
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
                if (handler != null) {
                    handler.handleMessage(message);
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