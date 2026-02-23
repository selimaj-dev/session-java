package dev.selimaj.session.types;

@FunctionalInterface
public interface MethodHandler<Req, Res, Err> {
    SessionResult<Res, Err> handle(int id, Req data);
}
