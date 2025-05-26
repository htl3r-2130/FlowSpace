
package com.example.flowspace_projectfolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private static String currentUser = null;

    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader in;

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

    public static void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }

    public static void sendTask(String taskText) {
        if (out != null && currentUser != null) {
            out.println("task|add|" + currentUser + "|" + taskText);
        } else {
            System.err.println("Error while sending task: " + taskText);
        }
    }

    public static void deleteTask(String taskText) {
        if (out != null && currentUser != null) {
            out.println("task|delete|" + currentUser + "|" + taskText);
        } else {
            System.err.println("Error while deleting task: " + taskText);
        }
    }
}