package dev.selimaj.session;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.selimaj.session.types.Message;
import dev.selimaj.session.types.MethodHandler;

public class SessionListener implements WebSocket.Listener {
    final ConcurrentHashMap<String, MethodHandler<JsonNode>> methods = new ConcurrentHashMap<>();

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
            MethodHandler<JsonNode> handler = methods.get(r.method());

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

    @Override
    public CompletionStage<?> onPing(WebSocket ws, ByteBuffer message) {
        return ws.sendPong(message);
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

    <Req, Res> CompletableFuture<Res> request(
            WebSocket ws,
            String method,
            Req data,
            Class<Res> resType) throws Exception {

        int id = this.id.incrementAndGet();

        CompletableFuture<JsonNode> fut = new CompletableFuture<>();
        pending.put(id, fut);

        send(ws, new Message.Request(
                id,
                method,
                mapper.valueToTree(data)));

        return fut.thenApply(json -> {
            try {
                return mapper.treeToValue(json, resType);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
