````md
# Java HTTP Server

A lightweight, multi-threaded HTTP server built from scratch using core Java.

The project implements HTTP request handling over TCP sockets, manual request parsing, dynamic routing, static file serving, JSON responses, concurrent connection handling, and basic security controls without using frameworks such as Spring Boot or Tomcat.

## Stack

- Java 17+
- `java.net`
- `java.io`
- `java.util.concurrent`
- TCP sockets
- HTTP
- HTML
- CSS
- JavaScript
- JSON

## Features

- Multi-threaded HTTP server using `ExecutorService`
- TCP connection handling with `ServerSocket` and `Socket`
- Manual HTTP request parsing
- Static file serving from the web root
- MIME type detection for static resources
- Dynamic request routing
- Query parameter parsing and URL decoding
- JSON API responses
- HTTP status code handling
- Basic directory traversal protection
- UTF-8 request and response handling
- Custom structured logging
- Separation of server, request handling, and logging responsibilities

## Project Structure

```text
JavaHttpServer/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── server/
│       │               ├── HttpServer.java
│       │               ├── RequestHandler.java
│       │               └── Logger.java
│       │
│       └── resources/
│           └── webroot/
│               └── index.html
│
├── README.md
└── .gitignore
````

### Core Components

| File                  | Purpose                                                      |
| --------------------- | ------------------------------------------------------------ |
| `HttpServer.java`     | Server entry point, socket setup, and thread pool management |
| `RequestHandler.java` | HTTP request parsing, routing, static files, and responses   |
| `Logger.java`         | Timestamped application logging                              |
| `webroot/`            | Static web resources                                         |

## HTTP Request Flow

```text
Client
  |
  | TCP connection
  v
ServerSocket
  |
  | accept()
  v
Socket
  |
  v
ExecutorService
  |
  v
RequestHandler
  |
  +---- Parse request
  |
  +---- Extract method and path
  |
  +---- Route request
  |
  +---- Generate response
  |
  v
HTTP Response
  |
  v
Client
```

## Supported Endpoints

### Home

```text
GET /
```

Returns the main web page from the configured web root.

### Server Time

```text
GET /api/time
```

Returns the current server time as JSON.

Example:

```json
{
  "time": "12:30:45"
}
```

### User API

```text
GET /api/user?name=Alice
```

Reads the `name` query parameter and returns JSON data.

Example:

```json
{
  "name": "Alice"
}
```

### About

```text
GET /about
```

Returns the application's About page.

### Static Resources

Static resources are served from:

```text
src/main/resources/webroot/
```

Example:

```text
GET /style.css
```

## HTTP Status Codes

The server handles common HTTP responses:

| Status | Meaning                      |
| ------ | ---------------------------- |
| `200`  | Successful request           |
| `400`  | Invalid request or parameter |
| `404`  | Resource not found           |
| `405`  | HTTP method not supported    |
| `500`  | Internal server error        |

## Setup

### Requirements

Install:

* JDK 17 or later
* Git
* IntelliJ IDEA or another Java IDE

### Clone

```bash
git clone https://github.com/KahlubDev/JavaHttpServer.git
cd JavaHttpServer
```

## Build and Run

### IntelliJ IDEA

Open the project in IntelliJ IDEA and run:

```text
src/com/example/server/HttpServer.java
```

### Command Line

Compile the project:

```bash
javac -d out src/com/example/server/*.java
```

Start the server:

```bash
java -cp out com.example.server.HttpServer
```

The server runs on:

```text
http://localhost:8080
```

## Testing

Test the server with a browser or `curl`.

### Home

```bash
curl http://localhost:8080
```

### Time API

```bash
curl http://localhost:8080/api/time
```

### User API

```bash
curl "http://localhost:8080/api/user?name=Alice"
```

### Missing Resource

```bash
curl http://localhost:8080/missing.html
```

The missing resource should return:

```text
404 Not Found
```

## Concurrency

The server uses a fixed thread pool through `ExecutorService`.

Example:

```java
ExecutorService threadPool =
        Executors.newFixedThreadPool(10);
```

Incoming connections are assigned to available worker threads instead of creating an unlimited number of threads.

This provides controlled concurrency and reduces unnecessary resource consumption under multiple simultaneous connections.

## Security

The server includes basic protections around static file handling.

### Directory Traversal

Requests containing paths such as:

```text
../../secret.txt
```

are rejected instead of being served.

Static resources are resolved within the configured web root.

### Input Handling

Query parameters are URL-decoded before processing.

Example:

```text
/api/user?name=John%20Doe
```

becomes:

```text
John Doe
```

The server also validates requests and returns appropriate HTTP error responses for invalid input and unsupported methods.

## Logging

The project includes a custom logger with timestamped log levels.

Example:

```text
[2026-09-01 23:42:10] [INFO] Server started on port 8080
[2026-09-01 23:42:15] [INFO] GET /api/time
[2026-09-01 23:42:18] [WARN] Resource not found: /missing.html
```

Supported log levels:

```text
INFO
WARN
ERROR
```

## What I Learned

This project was built to understand how an HTTP server works below the level of frameworks.

Key areas include:

* TCP socket programming
* `ServerSocket` and `Socket`
* Java input and output streams
* HTTP request parsing
* HTTP response construction
* Routing
* Static file serving
* Query parameter processing
* JSON responses
* Multi-threaded programming
* Thread pools
* Character encoding
* Input validation
* Directory traversal protection
* Error handling
* Application logging
* Separation of concerns

## Design Decisions

The server intentionally avoids external web frameworks.

Instead, core Java APIs handle:

```text
TCP connections
        |
        v
HTTP parsing
        |
        v
Request routing
        |
        v
Application logic
        |
        v
HTTP response
```

This makes the underlying HTTP and networking process visible and easier to study.

## Future Improvements

Planned improvements include:

* HTTP/1.1 keep-alive support
* Additional HTTP methods such as `POST`, `PUT`, and `DELETE`
* Stronger request validation
* Request size limits
* Connection timeouts
* External configuration
* JUnit test coverage
* More comprehensive security testing
* JSON serialization through Jackson or Gson

## Status

Work in progress.

The current implementation is suitable for learning and demonstrating fundamental Java networking, HTTP server architecture, concurrency, routing, and basic web security practices.

## License

This project is open source and intended for educational purposes.

