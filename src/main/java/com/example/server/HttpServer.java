package com.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private static final int PORT = 8080;
    private static final int MAX_THREADS = 50; // Sufficient for a simple server

    public static void main(String[] args) {
        Logger.info("Starting Java HTTP Server on port " + PORT + "...");
        Logger.info("Thread Pool size: " + MAX_THREADS + " threads");

        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Logger.info("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Submit task to the pool instead of creating a new thread
                threadPool.execute(new RequestHandler(clientSocket));
            }
        } catch (IOException e) {
            Logger.error("Server error", e);
        } finally {
            Logger.info("Shutting down server...");
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException ex) {
                threadPool.shutdownNow();
            }
        }
    }
}