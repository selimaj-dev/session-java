package dev.selimaj.session;

import com.fasterxml.jackson.databind.JsonNode;

import dev.selimaj.session.types.Message;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public final class Session {

    private final WebSocket ws;
    private final SessionListener listener;

    public Session(WebSocket ws, SessionListener listener) {
        this.ws = ws;
        this.listener = listener;
    }

    public static Session connect(String uri) throws Exception {
        SessionListener listener = new SessionListener();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(uri), listener)
                .join();

        return new Session(ws, listener);
    }

    public void send(Message msg) throws Exception {
        listener.send(ws, msg);
    }

    public void respond(int to, JsonNode val) throws Exception {
        listener.respond(ws, to, val);
    }

    public void respondError(int to, JsonNode val) throws Exception {
        listener.respondError(ws, to, val);
    }

    public void notify(String method, Object data) throws Exception {
        listener.notify(ws, method, data);
    }

    public void close() throws Exception {
        ws.sendClose(0, "Session closed");
    }
}