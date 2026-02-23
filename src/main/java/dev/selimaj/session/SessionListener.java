package dev.selimaj.session;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.selimaj.session.types.Message;
import dev.selimaj.session.types.MethodHandler;
import dev.selimaj.session.types.NotificationHandler;

public class SessionListener implements WebSocket.Listener {
    final ConcurrentHashMap<String, MethodHandler<JsonNode, JsonNode, JsonNode>> methods = new ConcurrentHashMap<>();
    final ConcurrentHashMap<String, NotificationHandler<JsonNode>> notificationHandlers = new ConcurrentHashMap<>();

    final ObjectMapper mapper = new ObjectMapper();
    private final ConcurrentHashMap<Integer, CompletableFuture<JsonNode>> pending = new ConcurrentHashMap<>();
    private final AtomicInteger id = new AtomicInteger();

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
            MethodHandler<JsonNode, JsonNode, JsonNode> handler = methods.get(r.method());

            if (handler != null) {
                try {
                    var res = handler.handle(r.id(), r.data());

                    if (res.isError()) {
                        respondError(ws, r.id(), res.getJsonNode(mapper));
                    } else {
                        respond(ws, r.id(), res.getJsonNode(mapper));
                    }
                } catch (Exception ignored) {
                }
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
        } else if (msg instanceof Message.Notification r) {
            NotificationHandler<JsonNode> handler = notificationHandlers.get(r.method());

            if (handler != null) {
                try {
                    handler.handle(r.data());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        ws.request(1);
        return null;
    }

    @Override
    public void onOpen(WebSocket ws) {
        ws.request(1);
    }

    @Override
    public CompletionStage<?> onPing(WebSocket ws, ByteBuffer message) {
        ws.request(1);
        return ws.sendPong(message);
    }

    @Override
    public CompletionStage<?> onPong(WebSocket ws, ByteBuffer message) {
        ws.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
        ws.request(1);
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

    public <Req, Res> CompletableFuture<Res> request(
            WebSocket ws,
            String method,
            Req data,
            Class<Res> resType) {

        int id = this.id.incrementAndGet();

        return CompletableFuture
                .runAsync(() -> {
                    try {
                        send(ws, new Message.Request(
                                id,
                                method,
                                mapper.valueToTree(data)));
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                })
                .thenCompose(v -> {
                    CompletableFuture<JsonNode> fut = new CompletableFuture<>();
                    pending.put(id, fut);
                    return fut;
                })
                .thenApply(json -> {
                    try {
                        return mapper.treeToValue(json, resType);
                    } catch (Exception e) {
                        throw new CompletionException(e);
                    }
                });
    }
}
