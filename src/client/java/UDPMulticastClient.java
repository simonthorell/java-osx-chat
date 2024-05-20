package client.java;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulticastClient implements IChatClient, Runnable {
    private MulticastSocket socket;
    private InetAddress group;
    private IMessageHandler handler;
    private volatile boolean running = true;

    @Override
    public void connect() throws IOException {
        socket = new MulticastSocket(Constants.MULTICAST_PORT);
        group = InetAddress.getByName(Constants.MULTICAST_GROUP_IP);
        socket.joinGroup(group);
        new Thread(this).start();
    }

    @Override
    public void sendMessage(ChatMessage message) throws IOException {
        byte[] buf = serialize(message);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, group, Constants.MULTICAST_PORT);
        socket.send(packet);
    }

    @Override
    public void disconnect() throws IOException {
        running = false;
        if (socket != null) {
            socket.leaveGroup(group);
            socket.close();
        }
    }

    @Override
    public void setMessageHandler(IMessageHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            while (running) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                ChatMessage message = deserialize(packet.getData());
                if (handler != null) {
                    handler.handleMessage(message);
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