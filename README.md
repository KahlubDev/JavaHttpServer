````md
# Java HTTP Server

A lightweight, multi-threaded HTTP server built from scratch using core Java technologies such as `java.net`, `java.io`, and `java.util.concurrent`.

This project demonstrates the fundamentals of network programming, HTTP request parsing, TCP socket communication, concurrency, routing, static file serving, and server-side architecture without relying on external frameworks such as Spring Boot or Tomcat.

## Architecture Overview

The server follows the standard HTTP request-response model over TCP sockets.

### HttpServer

Initializes the `ServerSocket` and manages an `ExecutorService` thread pool. The thread pool allows the server to process multiple client connections concurrently while controlling the number of active threads.

### RequestHandler

Processes individual client connections. It reads and parses the raw HTTP request, determines the requested resource, routes the request to the appropriate handler, and constructs the HTTP response.

### Logger

Provides a custom logging utility with timestamps and log levels such as `INFO`, `WARN`, and `ERROR`. This makes server activity easier to monitor and debug.

## Features

- Static file serving for HTML, CSS, JavaScript, and image files.
- Correct MIME type handling for supported static resources.
- Dynamic routing for custom endpoints such as `/api/time`, `/api/user`, and `/about`.
- Query parameter parsing and URL decoding using `URLDecoder`.
- JSON API responses.
- HTTP status code handling, including `200`, `400`, `404`, `405`, and `500`.
- Fixed thread pool for concurrent request processing.
- Basic directory traversal protection.
- UTF-8 character encoding for requests and responses.
- Structured server logging with timestamps and log levels.
- Separation of server initialization, request handling, routing, and logging responsibilities.

## Getting Started

### Prerequisites

Before running the project, ensure you have:

- Java Development Kit (JDK) 17 or higher.
- IntelliJ IDEA or another Java-compatible IDE.
- Git for cloning the repository.

### Clone the Repository

```bash
git clone https://github.com/KahlubDev/JavaHttpServer.git
cd JavaHttpServer
````

### Build and Run

#### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Locate `HttpServer.java`.
3. Right-click the file.
4. Select `Run`.

#### Using the Command Line

Compile the Java source files:

```bash
javac -d out src/com/example/server/*.java
```

Start the server:

```bash
java -cp out com.example.server.HttpServer
```

## Accessing the Server

Once the server starts, open a browser or use a terminal to access the following endpoints.

### Home Page

```text
http://localhost:8080
```

### API Time

```text
http://localhost:8080/api/time
```

### API User

```text
http://localhost:8080/api/user?name=Alice
```

### About Page

```text
http://localhost:8080/about
```

### Static File

```text
http://localhost:8080/style.css
```

The static file endpoint works when `style.css` exists inside the configured web root.

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
```

### Main Components

| File                  | Responsibility                                                               |
| --------------------- | ---------------------------------------------------------------------------- |
| `HttpServer.java`     | Server entry point, socket initialization, and thread pool management        |
| `RequestHandler.java` | HTTP request parsing, routing, response generation, and static file handling |
| `Logger.java`         | Application logging                                                          |
| `webroot/`            | Static HTML, CSS, JavaScript, and image resources                            |
| `README.md`           | Project documentation                                                        |
| `.gitignore`          | Files and directories excluded from version control                          |

## Key Concepts and Learnings

This project focuses on understanding core Java networking and server-side programming without hiding the underlying implementation behind a web framework.

### 1. Low-Level Networking

#### ServerSocket and Socket

The server uses `ServerSocket` to listen for incoming TCP connections and `Socket` to communicate with individual clients.

The basic connection flow is:

```text
Client
   |
   | TCP Connection
   v
ServerSocket
   |
   | accept()
   v
Socket
   |
   v
RequestHandler
```

#### Stream Handling

The server reads incoming HTTP data through an `InputStream` and sends responses through an `OutputStream`.

```java
InputStream input = socket.getInputStream();
OutputStream output = socket.getOutputStream();
```

This provides direct exposure to how HTTP data travels through a TCP connection.

#### HTTP Request Parsing

Instead of relying on a framework, the server manually processes HTTP request data such as:

```text
GET /api/time HTTP/1.1
Host: localhost:8080
Connection: close
```

The request line contains three important components:

```text
HTTP Method
     |
     v
GET /api/time HTTP/1.1
    |        |
    |        └── HTTP Version
    |
    └────────── Resource Path
```

### 2. Concurrency and Performance

#### Thread Pooling

The server uses `ExecutorService` to manage concurrent requests.

A fixed thread pool prevents the server from creating an unlimited number of threads when many clients connect simultaneously.

Example:

```java
ExecutorService threadPool = Executors.newFixedThreadPool(10);
```

The approach provides controlled concurrency and better resource management compared with creating a new thread for every request.

#### Thread Safety

Request handlers are designed to avoid unnecessary shared mutable state. Each client request receives its own handler execution context.

#### Blocking Operations

The server uses blocking socket operations such as:

```java
serverSocket.accept();
```

and:

```java
socket.getInputStream();
```

Understanding these operations provides a foundation for later learning about non-blocking I/O and asynchronous server architectures.

## Data Handling and Security

### UTF-8 Character Encoding

The server uses `StandardCharsets.UTF_8` when processing request and response data.

This provides consistent handling of characters across different clients and operating systems.

### URL Decoding

Query parameters are decoded using `URLDecoder`.

For example:

```text
/api/user?name=John%20Doe
```

is decoded into:

```text
John Doe
```

### Directory Traversal Protection

Static file requests are validated before files are served.

The server blocks malicious path patterns such as:

```text
../../secret.txt
```

This reduces the risk of attackers accessing files outside the configured web root.

## HTTP Status Codes

The server handles common HTTP response statuses.

| Status | Meaning               | Example                            |
| ------ | --------------------- | ---------------------------------- |
| `200`  | OK                    | Successful request                 |
| `400`  | Bad Request           | Invalid request or query parameter |
| `404`  | Not Found             | Requested resource does not exist  |
| `405`  | Method Not Allowed    | Unsupported HTTP method            |
| `500`  | Internal Server Error | Unexpected server-side failure     |

## API Endpoints

### GET /api/time

Returns the current server time.

Example request:

```http
GET /api/time HTTP/1.1
```

Example response:

```json
{
  "time": "12:30:45"
}
```

### GET /api/user

Accepts a `name` query parameter.

Example request:

```http
GET /api/user?name=Alice HTTP/1.1
```

Example response:

```json
{
  "name": "Alice"
}
```

### GET /about

Returns the application's About page.

## Request Processing Flow

A typical request follows this process:

```text
Client sends HTTP request
        |
        v
ServerSocket accepts connection
        |
        v
Thread pool assigns worker
        |
        v
RequestHandler reads request
        |
        v
HTTP method and path are parsed
        |
        v
Request is routed
        |
        +----------------------+
        |                      |
        v                      v
Static Resource          Dynamic Endpoint
        |                      |
        v                      v
Read File                Generate Response
        |                      |
        +----------+-----------+
                   |
                   v
             HTTP Response
                   |
                   v
                 Client
```

## Software Engineering Principles

### Separation of Concerns

Each major responsibility has its own component.

```text
HttpServer
    |
    +-- Server startup
    +-- Socket management
    +-- Thread pool

RequestHandler
    |
    +-- Request parsing
    +-- Routing
    +-- File serving
    +-- Response generation

Logger
    |
    +-- Application logging
```

This structure makes the project easier to understand and extend.

### Resource Management

The project uses try-with-resources where appropriate to ensure sockets and streams close correctly after use.

### Error Handling

The server handles invalid requests and missing resources with appropriate HTTP responses instead of terminating the entire application.

### Observability

The custom logger records server events with timestamps and severity levels.

Example:

```text
[2026-09-01 23:42:10] [INFO] Server started on port 8080
[2026-09-01 23:42:15] [INFO] GET /api/time
[2026-09-01 23:42:18] [WARN] Resource not found: /missing.html
```

## Testing the Server

You can test endpoints through a web browser or command-line tools such as `curl`.

### Test the Home Page

```bash
curl http://localhost:8080
```

### Test the Time API

```bash
curl http://localhost:8080/api/time
```

### Test the User API

```bash
curl "http://localhost:8080/api/user?name=Alice"
```

### Test a Missing Resource

```bash
curl http://localhost:8080/missing.html
```

The server should return an HTTP `404` response.

## Future Enhancements

The following improvements would extend the server's functionality.

### HTTP/1.1 Keep-Alive

Support persistent TCP connections so clients send multiple HTTP requests over the same connection.

### JSON Library Integration

Integrate a JSON library such as Jackson or Gson for more complex object serialization and deserialization.

### External Configuration

Move server settings such as the port number and thread pool size into a configuration file.

Example:

```properties
server.port=8080
server.thread-pool-size=10
```

### Unit Testing

Add JUnit tests for:

* Request parsing.
* URL decoding.
* Query parameter validation.
* Routing.
* HTTP status handling.
* Directory traversal protection.

### Improved Security

Future versions could add:

* Stronger path validation.
* HTTP header validation.
* Request size limits.
* Connection timeouts.
* Method validation.
* Improved error responses.
* Security-focused automated tests.

### Additional HTTP Methods

Add support for methods such as:

```text
POST
PUT
DELETE
```

This would allow the server to support more complete REST-style APIs.

## Technologies Used

* Java 17+
* `java.net`
* `java.io`
* `java.util.concurrent`
* TCP sockets
* HTTP
* JSON
* HTML
* CSS
* JavaScript
* Git
* GitHub

## Learning Objectives

By completing this project, I gained practical experience with:

* TCP socket programming.
* HTTP fundamentals.
* Manual HTTP request parsing.
* HTTP response construction.
* Java I/O streams.
* Multi-threaded programming.
* Thread pools.
* Static file serving.
* Dynamic routing.
* Query parameter processing.
* JSON API development.
* Input validation.
* Directory traversal protection.
* Error handling.
* Logging and observability.
* Separation of concerns.

## License

This project is open source and intended for educational purposes.

```
```
