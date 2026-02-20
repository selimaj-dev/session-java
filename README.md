<p align="center">
  <img src="logo.svg" alt="Session" width="150"/>
</p>
<h1 align="center">session-java</h1>
<p align="center">A lightweight, async WebSocket protocol for Java.</p>

## Introduction

This library provides **type-safe WebSocket communication** with a request-response and notification system built on top of a flexible protocol.  
It ensures compile-time guarantees for message structure, reduces runtime errors, and simplifies building Java client/server applications.

- **Dynamic Methods**: Each message includes a method enum for type safety.
- **Typed Requests & Responses**: Automatic serialization and deserialization.
- **Optional Notifications**: Send asynchronous notifications across sessions.

## Features

- Fully typed WebSocket sessions
- Type-safe request/response mechanism
- Optional typed notifications (Todo)
- Lightweight, minimal runtime overhead

## Installation

#### Add the repository

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

#### Add the dependency
```groovy
dependencies {
    implementation 'com.github.selimaj-dev:session-java:0.1.3'
}
```

---

### **Basic Example (client)**

```java
Session session = Session.connect("ws://localhost:8080/");

TextNode response = session.request(Methods.Data, TextNode.valueOf("Hello from client")).get();

System.out.println(response);

session.close();
```

### **Basic Example (server)**

#### ⚠️ Server functionality not done yet this is only for future examples

```java
SessionServer server = SessionServer.bind("ws://localhost:8080/");

server.onClient((session, addr) -> {
    session.onRequest(Methods.Data, (id, data) -> {
        return SessionResult.ok(TextNode.valueOf("Response from server"));
    });
});
```

## Protocol

#### Request

The request `id` is separated from the peer, and will increment only on it's requests.

```json
{ "type": "request", "id": 1, "method": "data", "data": "Hello from client" }
```

#### Response

The response `id` **must** remain the same as the request.

```json
{ "type": "response", "id": 1, "result": "Hello from server" }
```

#### Notifications

A notification is a method that doesn't need validation or output, it simply notifies a peer for a specific information

```json
{ "type": "notification", "result": "Hello from server" }
```
