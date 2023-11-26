import java.io.*;
import java.net.*;

public class ChatUser extends Thread {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username = "Anonymous";
    private ChatServer server;

    public ChatUser(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String input;
            while ((input = reader.readLine()) != null) {

                if (input.startsWith("/username")) {
                    String[] parts = input.split(" ", 2);
                    if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                        username = parts[1].trim();
                        sendMessage("Your username is now: " + username);
                    } else {
                        sendMessage("Invalid username. Please use '/username [new_username]'");
                    }
                }

                else if (input.equalsIgnoreCase("/exit")) {
                    // Exit command
                    sendMessage("You have left the chat.");
                    server.removeUser(this);
                    break;
                }
                else if (input.startsWith("/private")) {
                    // Private message command
                    String[] parts = input.split(" ", 3);
                    if (parts.length >= 3 && !parts[1].trim().isEmpty() && !parts[2].trim().isEmpty()) {
                        String targetUser = parts[1].trim();
                        String message = parts[2].trim();
                        sendPrivateMessage(targetUser, "[" + username + "]: " + message);
                    } else {
                        sendMessage("Invalid private message format. Please use '/private [username] [message]'");
                    }
                } else {
                    // Broadcast message to all users
                    server.broadcastMessage("[" + username + "]: " + input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public void sendPrivateMessage(String targetUser, String message) {
        for (ChatUser user : server.clients) {
            if (user.username.equalsIgnoreCase(targetUser)) {
                user.sendMessage("[Private from " + username + "]: " + message);
                return;
            }
        }
        sendMessage("User '" + targetUser + "' not found or offline.");
    }
}