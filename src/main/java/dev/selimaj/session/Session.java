package dev.selimaj.session;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

import dev.selimaj.session.types.SessionResult;
import dev.selimaj.session.types.Message;
import dev.selimaj.session.types.Method;
import dev.selimaj.session.types.MethodHandler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    public static Session connect(String uri, long timeout, TimeUnit unit) throws Exception {
        SessionListener listener = new SessionListener();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(uri), listener)
                .orTimeout(timeout, unit)
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

    public <Req, Res, Err> CompletableFuture<Res> request(
            Method<Req, Res, Err> method, Req req) throws Exception {
        return listener.request(ws, method.getName(), req, method.getResClass());
    }

    public <Req, Res, Err> void onRequest(Method<Req, Res, Err> method,
            MethodHandler<Req> handler) {
        MethodHandler<JsonNode> wrapper = (id, value) -> {
            try {
                return handler.handle(id, listener.mapper.treeToValue(value, method.getReqClass()));
            } catch (Exception e) {
                return SessionResult.error(TextNode.valueOf(e.getMessage()));
            }
        };

        this.listener.methods.put(method.getName(), wrapper);
    }

    public void close() throws Exception {
        ws.sendClose(0, "Session closed");
    }
}