package dev.selimaj.session.types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public record SessionResult<T, E>(boolean isError, Object value) {
    public static <T, E> SessionResult<T, E> ok(T value) {
        return new SessionResult<>(false, value);
    }

    public static <T, E> SessionResult<T, E> error(E err) {
        return new SessionResult<>(false, err);
    }

    public SessionResult<JsonNode, JsonNode> intoJSON(ObjectMapper mapper) {
        return new SessionResult<>(this.isError, mapper.valueToTree(this.value));
    }

    public JsonNode getJsonNode(ObjectMapper mapper) {
        return mapper.valueToTree(this.value);
    }
}
