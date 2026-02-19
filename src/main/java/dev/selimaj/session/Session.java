package dev.selimaj.session;

import java.util.concurrent.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;

public final class Session {

    private final WebSocket ws;
    private final ExecutorService executor;
    private final SessionListener listener;

    public Session(WebSocket ws, SessionListener listener, ExecutorService executor) {
        this.ws = ws;
        this.executor = executor;
        this.listener = listener;
    }

    public static Session connect(String uri) throws Exception {
        SessionListener listener = new SessionListener();

        WebSocket ws = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create("ws://example.com/echo"), listener)
                .join();

        return new Session(ws, listener, Executors.newCachedThreadPool());
    }

    public void startReceiver() {
        executor.execute(() -> {

        });
    }

}