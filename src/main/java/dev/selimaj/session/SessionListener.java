package dev.selimaj.session;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.selimaj.session.types.Message;
import dev.selimaj.session.types.MethodHandler;

public class SessionListener implements WebSocket.Listener {
    public final ConcurrentHashMap<String, MethodHandler> methods = new ConcurrentHashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();
    private final ConcurrentHashMap<Integer, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();

    @Override
    public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
        Message msg = null;

        try {
            msg = mapper.readValue(data.toString(), Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (msg instanceof Message.Request r) {
            MethodHandler handler = methods.get(r.method());

            if (handler != null) {
                handler.handle(r.id(), r.data())
                        .thenAccept(res -> {
                            try {
                                if (res.isError()) {
                                    respondError(ws, r.id(), res.value());
                                } else {
                                    respond(ws, r.id(), res.value());
                                }
                            } catch (Exception ignored) {
                            }
                        });
            }
        } else if (msg instanceof Message.Response r) {
            CompletableFuture<JsonNode> fut = pending.remove(r.id());
            if (fut != null)
                fut.complete(r.result());
        } else if (msg instanceof Message.ErrorResponse r) {
            CompletableFuture<JsonNode> fut = pending.remove(r.id());
            if (fut != null)
                fut.completeExceptionally(
                        new RuntimeException(r.error().toString()));
        }

        return null;
    }

    void send(WebSocket ws, Message msg) throws Exception {
        ws.sendText(mapper.writeValueAsString(msg), true);
    }

    void respond(WebSocket ws, int to, JsonNode val) throws Exception {
        send(ws, new Message.Response(to, val));
    }

    void respondError(WebSocket ws, int to, JsonNode val) throws Exception {
        send(ws, new Message.ErrorResponse(to, val));
    }

    void notify(WebSocket ws, String method, Object data) throws Exception {
        send(ws, new Message.Notification(method, mapper.valueToTree(data)));
    }
}
