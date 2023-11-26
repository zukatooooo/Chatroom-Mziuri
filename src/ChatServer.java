import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private ServerSocket serverSocket;
    List<ChatUser> clients = new ArrayList<>();

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start(9090);
    }

    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                ChatUser chatUser = new ChatUser(socket, this);
                clients.add(chatUser);
                chatUser.start();

                chatUser.sendMessage("Welcome to the chat room!");
                broadcastMessage("New user joined. Total users: " + clients.size());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message) {
        for (ChatUser client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeUser(ChatUser user) {
        clients.remove(user);
        broadcastMessage("A user has left. Total users: " + clients.size());
    }
}