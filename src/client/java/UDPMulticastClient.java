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
    private final String requestUsersMsg = "username_request";
    private final String responseUsersMsg = "user_response";
    private List<String> users = new ArrayList<>();

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

        // Broadcast a request for active users
        sendMessage(new ChatMessage(username, requestUsersMsg));

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

//    @Override
//    public List<String> getUsers() {
//        return users;
//    }

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
            System.out.println("Adding user: " + username);
            users.add(username);
        }
    }

    @Override
    public void removeUser(String username) {
        users.remove(username);
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

                // TODO: Add logic to handle new connection and disconnection messages

                if (message.getMessage().equals(requestUsersMsg)) {
                    // Respond with the current user's username to the group
                    sendMessage(new ChatMessage(username, "user_response"));
                } else if (message.getMessage().equals(responseUsersMsg)) {
                    // Add the user to the list of active users
                    addUser(message.getUser());
                } else if (handler != null) {
                    handler.handleMessage(message);
                }

//                if (handler != null) {
//                    handler.handleMessage(message);
//                }
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