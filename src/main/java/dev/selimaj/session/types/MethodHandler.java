package dev.selimaj.session.types;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
public interface MethodHandler<Req extends JsonNode> {
    CompletableFuture<SessionResult> handle(int id, Req data);
}
