package server.java;

import common.Constants;

public class Main {
  public static void main(String[] args) {
    ChatServer server = new ChatServer(Constants.TCP_PORT);
    server.startServer();
  }
}
