package com.example.flowspace_projectfolder;

import java.io.*;
import java.net.Socket;

public class NetworkManager {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;

    public static boolean login(String username, String password) {
        try (
                Socket socket = new Socket(SERVER_ADDRESS, PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("login|" + username + "|" + password);
            String response = in.readLine();
            if (response.equals("OK")) {
                return true;
            }
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
        return false;
    }
}
