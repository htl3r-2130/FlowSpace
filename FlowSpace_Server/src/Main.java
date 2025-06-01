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
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 0) {
                    out.println("ERROR");
                    continue;
                }
                String command = parts[0];

                switch (command) {
                    case "login" -> handleLogin(parts, out);
                    case "task" -> handleTask(parts, out);
                    case "signup" -> handleSignup(parts, out);
                    case "account" -> handleAccount(parts, out);
                    case "config" -> handleConfig(parts, out);
                    default -> out.println("ERROR");
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

    private static void handleLogin(String[] parts, PrintWriter out) {
        if (parts.length != 3) {
            out.println("ERROR");
            return;
        }

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
    }

    private static void handleTask(String[] parts, PrintWriter out) {
        if (parts.length != 5) {
            out.println("ERROR");
            return;
        }
        String action = parts[1];
        String username = parts[2];
        String date = parts[3]; // YYYY-MM-DD
        String taskText = parts[4];
        String fullLine = date + "|" + taskText;
        File userFile = new File("userFiles/" + username + ".txt");
        synchronized (("lock_" + username).intern()) {
            switch (action) {
                case "add" -> {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
                        writer.write(fullLine);
                        writer.newLine();
                        System.out.println("  -> task added - user:" + username + " | " + fullLine);
                        out.println("OK");
                    } catch (IOException e) {
                        System.err.println("Error writing task: " + e.getMessage());
                        out.println("ERROR");
                    }
                }
                case "delete" -> {
                    if (!userFile.exists()) {
                        out.println("ERROR");
                        return;
                    }
                    try {
                        List<String> lines = new ArrayList<>();
                        boolean removed = false;
                        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                if (!removed && line.equals(fullLine)) {
                                    removed = true;
                                    continue;
                                }
                                lines.add(line);
                            }
                        }
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile))) {
                            for (String line : lines) {
                                writer.write(line);
                                writer.newLine();
                            }
                        }
                        System.out.println("  -> task removed - user:" + username + " | " + fullLine);
                        out.println("OK");
                    } catch (IOException e) {
                        System.err.println("Error modifying task file: " + e.getMessage());
                        out.println("ERROR");
                    }
                }
                case "get" -> {
                    if (!userFile.exists()) {
                        out.println("END");
                        return;
                    }
                    try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            out.println(line); // Send: YYYY-MM-DD|Task text
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    out.println("END");
                }
                default -> out.println("ERROR");
            }
        }
    }


    private static synchronized void handleSignup(String[] parts, PrintWriter out) {
        if (parts.length != 3) {
            out.println("ERROR");
            return;
        }
        String username = parts[1];
        String password = parts[2];
        if (users.containsKey(username)) {
            System.out.println("  -> Signup failed: username '" + username + "' already exists.");
            out.println("ERROR");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.newLine();
            writer.write(username + "=" + password);
        } catch (IOException e) {
            System.err.println("Error saving new user: " + e.getMessage());
            out.println("ERROR");
            return;
        }
        File calendarFile = new File("userFiles/" + username + ".txt");
        try {
            if (calendarFile.createNewFile()) {
                System.out.println("  -> Signup successful: '" + username + "' registered.");
                loadUsers();
                out.println("OK");
            } else {
                System.err.println("Error: Could not create calendar file for user '" + username + "'");
                out.println("ERROR");
            }
        } catch (IOException e) {
            System.err.println("Error creating calendar file: " + e.getMessage());
            out.println("ERROR");
        }
    }

    private static void handleAccount(String[] parts, PrintWriter out) {
        if (parts.length < 3) {
            out.println("ERROR");
            return;
        }

        String action = parts[1];
        String username = parts[2];

        switch (action) {
            case "changePassword" -> {
                if (parts.length != 4) {
                    out.println("ERROR");
                    return;
                }
                String newPassword = parts[3];
                users.put(username, newPassword);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
                    for (Map.Entry<String, String> entry : users.entrySet()) {
                        writer.write(entry.getKey() + "=" + entry.getValue());
                        writer.newLine();
                    }
                    System.out.println("  -> password changed for: " + username);
                    out.println("OK");
                } catch (IOException e) {
                    System.err.println("Error saving password: " + e.getMessage());
                    out.println("ERROR");
                }
            }

            case "delete" -> {
                users.remove(username);
                File userFile = new File("userFiles/" + username + ".txt");
                boolean deletedData = userFile.delete();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
                    for (Map.Entry<String, String> entry : users.entrySet()) {
                        writer.write(entry.getKey() + "=" + entry.getValue());
                        writer.newLine();
                    }
                } catch (IOException e) {
                    System.err.println("Error updating user list: " + e.getMessage());
                    out.println("ERROR");
                    return;
                }

                if (deletedData) {
                    System.out.println("  -> account deleted: " + username);
                    out.println("OK");
                } else {
                    out.println("ERROR");
                }
            }

            default -> out.println("ERROR");
        }
    }

    private static void handleConfig(String[] parts, PrintWriter out) {
        if (parts.length < 3) {
            out.println("ERROR");
            return;
        }
        String action = parts[1];
        String username = parts[2];
        File configFile = new File("userFiles/" + username + ".txt");

        switch (action) {
            case "get" -> {
                if (!configFile.exists()) {
                    out.println("END");
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.println(Base64.getEncoder().encodeToString(line.getBytes()));
                    }
                    out.println("END");
                } catch (IOException e) {
                    out.println("ERROR");
                }
            }
            case "put" -> {
                if (parts.length != 4) {
                    out.println("ERROR");
                    return;
                }
                try {
                    byte[] decoded = Base64.getDecoder().decode(parts[3]);
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
                        writer.write(new String(decoded));
                    }
                    out.println("OK");
                } catch (Exception e) {
                    out.println("ERROR");
                }
            }
            default -> out.println("ERROR");
        }
    }
}
