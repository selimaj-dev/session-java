package dev.selimaj.session;

import com.fasterxml.jackson.databind.JsonNode;

import dev.selimaj.session.types.Message;
import dev.selimaj.session.types.Method;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;

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

    <Req extends JsonNode, Res extends JsonNode, Err extends JsonNode> CompletableFuture<Res> request(
            Class<? extends Method<Req, Res, Err>> method, Req req) throws Exception {
        Method<Req, Res, Err> m = method.getDeclaredConstructor().newInstance();
        return listener.request(ws, m.getName(), req, m.getResClass());
    }

    public void close() throws Exception {
        ws.sendClose(0, "Session closed");
    }
}