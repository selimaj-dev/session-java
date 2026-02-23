package dev.selimaj.session.types;

@FunctionalInterface
public interface NotificationHandler<Req> {
    void handle(Req data);
}
