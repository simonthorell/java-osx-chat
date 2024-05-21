package client.java.net;

import client.java.ChatMessage;
import client.java.IChatClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TCPClient implements IChatClient, Runnable{
    private final String username;
    private IMessageHandler handler;
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
        // TODO: Implement
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        // TODO: Implement
    }

    @Override
    public void disconnect(IUserListObserver observer) throws IOException {
        // TODO: Implement
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
        // TODO: Implement
    }

    @Override
    public void removeUser(String username) {
        // TODO: Implement
    }

    @Override
    public void addUserListObserver(IUserListObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeUserListObserver(IUserListObserver observer) {
        observers.remove(observer);
    }

    // Helper method to notify observers of changes to the user list
    private void notifyUserListChanged() {
//        for (IUserListObserver observer : observers) {
//            observer.userListUpdated(new ArrayList<>(users));
//        }
    }

    //====================================================================================================
    // Thread Methods (Runnable Interface)
    //====================================================================================================
    @Override
    public void run() {
        // TODO: Implement
    }