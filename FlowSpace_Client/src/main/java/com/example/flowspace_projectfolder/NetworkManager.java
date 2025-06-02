
package com.example.flowspace_projectfolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class NetworkManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private static String currentUser = null;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

    //Connects to server
    public static boolean connect() {
        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    //Trys to get a validation with given username & password
    public static boolean login(String username, String password) {
        try {
            out.println("login|" + username + "|" + password);
            String response = in.readLine();
            if ("OK".equalsIgnoreCase(response)) {
                currentUser = username;
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    //Closes the connection
    public static void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {
        }
    }

    //Sends task with format : task|add|user|YYYY-MM-DD|text
    public static void sendTask(String formattedTaskText) {
        if (out != null && currentUser != null) {
            out.println("task|add|" + currentUser + "|" + formattedTaskText);
        } else {
            System.err.println("Error while sending task: " + formattedTaskText);
        }
    }

    //Deletes task with format : task|delete|user|YYYY-MM-DD|text
    public static void deleteTask(String formattedTaskText) {
        if (out != null && currentUser != null) {
            out.println("task|delete|" + currentUser + "|" + formattedTaskText);
        } else {
            System.err.println("Error while deleting task: " + formattedTaskText);
        }
    }

    //Attempts signup with given parameters and checks if user already exists
    public static boolean signup(String username, String password) {
        if (!connect()) return false;
        try {
            out.println("signup|" + username + "|" + password);
            String response = in.readLine();
            return "OK".equals(response);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Returns all tasks for current user with a list
    public static List<String> loadUserTasks() {
        List<String> tasks = new ArrayList<>();
        if (out != null && currentUser != null) {
            try {
                out.println("task|get|" + currentUser + "|-|-");
                String line;
                while ((line = in.readLine()) != null && !line.equals("END")) {
                    tasks.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    //Attempts password change for current user
    public static boolean changePassword(String newPassword) {
        if (out != null && currentUser != null) {
            try {
                out.println("account|changePassword|" + currentUser + "|" + newPassword);
                String response = in.readLine();
                return "OK".equals(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Attempts deletion of current user
    public static boolean deleteAccount() {
        if (out != null && currentUser != null) {
            try {
                out.println("account|delete|" + currentUser + "|-");
                String response = in.readLine();
                if ("OK".equals(response)) {
                    currentUser = null;
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //Attempts to retrieve config file of current user
    public static String downloadConfig() {
        if (out != null && currentUser != null) {
            try {
                out.println("config|get|" + currentUser);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null && !line.equals("END")) {
                    sb.append(new String(Base64.getDecoder().decode(line))).append("\n");
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Attempts to upload new config file for current user
    public static boolean uploadConfig(String content) {
        if (out != null && currentUser != null) {
            try {
                String encoded = Base64.getEncoder().encodeToString(content.getBytes());
                out.println("config|put|" + currentUser + "|" + encoded);
                String response = in.readLine();
                return "OK".equals(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}