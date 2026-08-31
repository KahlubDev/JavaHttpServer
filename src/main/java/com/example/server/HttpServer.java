package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println(" Starting Java HTTP Server on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Block until a client connects
                Socket clientSocket = serverSocket.accept();

                // Handle each client in a new thread immediately
                new Thread(new RequestHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println(" Server crashed: " + e.getMessage());
        }
    }
}