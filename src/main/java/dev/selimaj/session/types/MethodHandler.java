package dev.selimaj.session.types;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface MethodHandler<Req> {
    CompletableFuture<SessionResult> handle(int id, Req data);
}
