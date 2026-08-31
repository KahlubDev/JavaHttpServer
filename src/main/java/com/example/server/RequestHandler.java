package com.example.server;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            // 1. Read the first line of the request (e.g., "GET / HTTP/1.1")
            String requestLine = in.readLine();
            if (requestLine == null) return;

            System.out.println(" Received: " + requestLine);

            // 2. Consume the rest of the headers (we don't need them for this demo)
            while (in.readLine() != null && !in.readLine().isEmpty()) {
                // Just skipping headers
            }

            // 3. Prepare the HTML Response
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String htmlBody = "<!DOCTYPE html><html><body>" +
                    "<h1> Success! Java HTTP Server is working.</h1>" +
                    "<p>Server received your request at: <b>" + timestamp + "</b></p>" +
                    "<p>Request: " + requestLine + "</p>" +
                    "</body></html>";

            // 4. Send HTTP Headers + Body
            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + htmlBody.getBytes("UTF-8").length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n" + // Empty line separates headers from body
                    htmlBody;

            out.print(response);
            out.flush();

        } catch (IOException e) {
            System.err.println(" Error handling client: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) { /* ignore */ }
        }
    }
}