package dev.selimaj.session;

import java.net.http.WebSocket;
import java.util.concurrent.ConcurrentHashMap;

import dev.selimaj.session.types.MethodHandler;

public class SessionListener implements WebSocket.Listener {
    public final ConcurrentHashMap<String, MethodHandler> methods = new ConcurrentHashMap<>();
}
