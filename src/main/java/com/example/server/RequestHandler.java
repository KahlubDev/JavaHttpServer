package com.example.server;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) {
                return;
            }

            System.out.println("Received: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts.length > 1 ? parts[1] : "/";

            // Consume HTTP headers
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                // Skip headers
            }

            // Route handling
            if ("GET".equals(method)) {
                handleGet(out, path);
            } else if ("POST".equals(method)) {
                handlePost(out, path, in);
            } else {
                sendError(out, 405, "Method Not Allowed");
            }

        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore close errors
            }
        }
    }

    private void handleGet(PrintWriter out, String path) {
        // Check static files first
        try {
            if (serveStaticFile(out, path)) {
                return;
            }
        } catch (IOException e) {
            System.err.println("Error serving file: " + e.getMessage());
        }

        // Split path and query string
        String[] parts = path.split("\\?", 2);
        String cleanPath = parts[0];
        String queryString = parts.length > 1 ? parts[1] : "";

        Map<String, String> queryParams = parseQueryString(queryString);

        // Route handling
        if ("/".equals(cleanPath)) {
            // Serve static index.html (handled above) or fallback
            String body = "<h1>Home Page</h1><p>Use <b>/api/time</b> or <b>/api/user?name=...</b> for API.</p>";
            sendHtml(out, 200, "OK", body);
        } else if ("/api/time".equals(cleanPath)) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Map<String, String> data = new HashMap<>();
            data.put("timestamp", time);
            sendJson(out, 200, "OK", data);
        } else if ("/api/user".equals(cleanPath)) {
            String name = queryParams.get("name");
            if (name != null && !name.isEmpty()) {
                Map<String, String> data = new HashMap<>();
                data.put("message", "Hello, " + name + "!");
                data.put("name", name);
                data.put("status", "active");
                sendJson(out, 200, "OK", data);
            } else {
                sendError(out, 400, "Missing 'name' parameter");
            }
        } else if ("/api/users".equals(cleanPath)) {
            // Example: Return a list of users (static for now)
            String response = "{\"users\": [" +
                    "{\"id\": 1, \"name\": \"Alice\", \"role\": \"admin\"}," +
                    "{\"id\": 2, \"name\": \"Bob\", \"role\": \"user\"}" +
                    "]}";
            sendJson(out, 200, "OK", response);
        } else if ("/about".equals(cleanPath)) {
            String body = "<h1>About</h1><p>This is a custom Java HTTP server.</p>";
            sendHtml(out, 200, "OK", body);
        } else {
            sendError(out, 404, "Not Found");
        }
    }

    private boolean serveStaticFile(PrintWriter out, String path) throws IOException {
        String filePath = path;
        if ("/".equals(path)) {
            filePath = "/index.html";
        }

        if (filePath.contains("..") || filePath.contains("\\")) {
            System.out.println("Blocked path: " + path);
            return false;
        }

        String resourcePath = "webroot" + filePath;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream != null) {
            byte[] fileBytes = inputStream.readAllBytes();
            inputStream.close();

            String contentType = "text/html";
            if (filePath.endsWith(".css")) {
                contentType = "text/css";
            } else if (filePath.endsWith(".js")) {
                contentType = "application/javascript";
            } else if (filePath.endsWith(".png")) {
                contentType = "image/png";
            } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (filePath.endsWith(".gif")) {
                contentType = "image/gif";
            }

            String responseHeaders = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            out.print(responseHeaders);
            out.flush();
            socket.getOutputStream().write(fileBytes);
            socket.getOutputStream().close();

            return true;
        }

        return false;
    }

    private void handlePost(PrintWriter out, String path, BufferedReader in) throws IOException {
        if ("/api/echo".equals(path)) {
            StringBuilder bodyContent = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                bodyContent.append(line);
            }
            String body = "<h1>Echo Response</h1><pre>" + bodyContent.toString() + "</pre>";
            sendHtml(out, 200, "OK", body);
        } else {
            sendError(out, 404, "Not Found");
        }
    }

    private void sendHtml(PrintWriter out, int status, String statusText, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.print(response);
        out.write(body);
        out.flush();
    }

    private void sendJson(PrintWriter out, int status, String statusText, String body) {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String response = "HTTP/1.1 " + status + " " + statusText + "\r\n" +
                "Content-Type: application/json; charset=UTF-8\r\n" +
                "Content-Length: " + bodyBytes.length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n";
        out.print(response);
        out.write(body);
        out.flush();
    }

    private void sendJson(PrintWriter out, int status, String statusText, Map<String, String> data) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        int i = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (i > 0) {
                json.append(",");
            }
            json.append("\"").append(keyEscape(entry.getKey())).append("\":\"").append(valueEscape(entry.getValue())).append("\"");
            i++;
        }
        json.append("}");
        sendJson(out, status, statusText, json.toString());
    }

    private void sendError(PrintWriter out, int status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("status", String.valueOf(status));
        sendJson(out, status, "Error", error);
    }

    private String keyEscape(String key) {
        return key.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String valueEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Map<String, String> parseQueryString(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return params;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }
}