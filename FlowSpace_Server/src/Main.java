import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    private static final int PORT = 12345;
    private static final File USER_FILE = new File("users.txt");
    private static final Map<String, String> users = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        loadUsers();
        System.out.println("- Server started on port: " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("- Client connected: " + socket.getInetAddress().getHostAddress());
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void loadUsers() {
        if (!USER_FILE.exists()) {
            System.out.println("- User file does not exist");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
            System.out.println("- Users loaded: " + users.keySet());
        } catch (IOException e) {
            System.err.println("Error while loading users: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("login|")) {
                    String[] parts = line.split("\\|", 3);
                    if (parts.length == 3) {
                        String username = parts[1];
                        String password = parts[2];

                        System.out.println("- Login attempt from '" + username + "'");

                        String storedPassword = users.get(username);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            System.out.println("  -> Login successful for '" + username + "'");
                            out.println("OK");
                        } else {
                            System.out.println("  -> Login failed for '" + username + "'");
                            out.println("ERROR");
                        }
                    } else {
                        out.println("ERROR");
                    }
                }
                else if (line.startsWith("task|")) {
                    String[] parts = line.split("\\|", 4);
                    if (parts.length == 4) {
                        String action = parts[1];
                        String username = parts[2];
                        String taskText = parts[3];

                        if (action.equals("add")) {
                            System.out.println("  -> task added - user:" + username + " | text: " + taskText);
                            out.println("OK");
                        } else if (action.equals("delete")) {
                            System.out.println("  -> task removed - user:" + username + " | text: " + taskText);
                            out.println("OK");
                        } else {
                            out.println("ERROR");
                        }
                    } else {
                        out.println("ERROR");
                    }
                }


            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("- Client disconnected: " + socket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
}
