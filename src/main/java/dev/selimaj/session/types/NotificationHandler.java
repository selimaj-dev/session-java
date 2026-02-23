package dev.selimaj.session.types;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface NotificationHandler<Req, Res, Err> {
    CompletableFuture<SessionResult<Res, Err>> handle(int id, Req data);
}
