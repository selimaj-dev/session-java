package dev.selimaj.session.types;

import com.fasterxml.jackson.databind.JsonNode;

public record HandlerResult(boolean isError, JsonNode value) {
}
