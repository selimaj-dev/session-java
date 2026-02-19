package dev.selimaj.session.types;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Message.Request.class, name = "request"),
        @JsonSubTypes.Type(value = Message.Response.class, name = "response"),
        @JsonSubTypes.Type(value = Message.ErrorResponse.class, name = "errorresponse"),
        @JsonSubTypes.Type(value = Message.Notification.class, name = "notification")
})
public sealed interface Message
        permits Message.Request, Message.Response, Message.ErrorResponse, Message.Notification {

    record Request(int id, String method, JsonNode data) implements Message {
    }

    record Response(int id, JsonNode result) implements Message {
    }

    record ErrorResponse(int id, JsonNode error) implements Message {
    }

    record Notification(String method, JsonNode data) implements Message {
    }
}
