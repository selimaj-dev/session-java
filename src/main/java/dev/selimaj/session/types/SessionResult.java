package dev.selimaj.session.types;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.JsonNode;

public record SessionResult(boolean isError, JsonNode value) {
    public static CompletableFuture<SessionResult> ok(JsonNode value) {
        return CompletableFuture.completedFuture(new SessionResult(false, value));
    }

    public static CompletableFuture<SessionResult> error(JsonNode value) {
        return CompletableFuture.completedFuture(new SessionResult(false, value));
    }
}
