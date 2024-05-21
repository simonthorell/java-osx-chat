package client.java;

import client.java.ui.ChatWindow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.util.ArrayList;
import java.util.List;

public class UDPMulticastClient implements IChatClient, Runnable {
    private MulticastSocket socket;
    private InetAddress group;
    private IMessageHandler handler;
    private volatile boolean running = true;
    private final String username;
    private List<String> users = new ArrayList<>();
    private List<UserListObserver> observers = new ArrayList<>();

    //====================================================================================================
    // Constructors
    //====================================================================================================
    public UDPMulticastClient(String username) {
        this.username = username;
    }

    //====================================================================================================
    // IChatClient Methods
    //====================================================================================================
    @Override
    public void connect() throws IOException {
        socket = new MulticastSocket(Constants.MULTICAST_PORT);
        group = InetAddress.getByName(Constants.MULTICAST_GROUP_IP);
        socket.joinGroup(group);
        new Thread(this).start();

        // Broadcast a request for active users usernames
        sendMessage(new ChatMessage(username, MessageType.USERNAME_REQUEST));

        // Notify other clients that this user has entered the chat room
        sendMessage(new ChatMessage(username, "has joined the chat room!"));
        addUser(username);
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        byte[] buf = serialize(message);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Constants.MULTICAST_PORT);
        socket.send(packet);
    }

    @Override
    public void disconnect() throws IOException {
        // Notify other clients that this user has left the chat room
        sendMessage(new ChatMessage(username, "has left the chat room!"));
        sendMessage(new ChatMessage(username, MessageType.USER_DISCONNECT));
        removeUser(username);

        // Close the socket
        if (socket != null) {
            socket.leaveGroup(group);
            socket.close();
        }
    }

    //====================================================================================================
    // Getters & Setters
    //====================================================================================================
    @Override
    public void setMessageHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public String[] getUsers() {
        System.out.println("Getting users: " + users);
        return users.toArray(new String[0]); // Convert List to String array
    }

    //====================================================================================================
    // User Management
    //====================================================================================================
    @Override
    public void addUser(String username) {
        if (!users.contains(username)) {
            // Avoid duplicates option
            users.add(username);
            notifyUserListChanged();
        }
    }

    @Override
    public void removeUser(String username) {
        users.remove(username);
        notifyUserListChanged();
    }

    //====================================================================================================
    // Observer Methods (UserListObserver for UI)
    //====================================================================================================
    private void notifyUserListChanged() {
        for (UserListObserver observer : observers) {
            observer.userListUpdated(new ArrayList<>(users));
        }
    }

    public void addUserListObserver(UserListObserver observer) {
        observers.add(observer);
    }

    public void removeUserListObserver(UserListObserver observer) {
        observers.remove(observer);
    }

    //====================================================================================================
    // Thread Methods
    //====================================================================================================
    @Override
    public void run() {
        try {
            while (running) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                ChatMessage message = deserialize(packet.getData());

                switch (message.getMessageType()) {
                    case USERNAME_REQUEST:
                        // Respond with the current user's username to the group
                        sendMessage(new ChatMessage(username, MessageType.USER_RESPONSE));
                        break;
                    case USER_RESPONSE:
                        // Add the user to the list of active users
                        addUser(message.getUser());
                        break;
                    case USER_DISCONNECT:
                        // Remove the user from the list of active users
                        removeUser(message.getUser());
                        break;
                    default:
                        // Handle all other messages (assumed to be chat messages)
                        if (handler != null) {
                            handler.handleMessage(message);
                        }
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private byte[] serialize(ChatMessage message) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(message);
            return bos.toByteArray();
        }
    }

    private ChatMessage deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (ChatMessage) ois.readObject();
        }
    }
}