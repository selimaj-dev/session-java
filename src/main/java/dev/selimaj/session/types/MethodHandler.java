package dev.selimaj.session.types;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MethodHandler {
    CompletableFuture<HandlerResult> handle(int id, JsonNode data);
}
