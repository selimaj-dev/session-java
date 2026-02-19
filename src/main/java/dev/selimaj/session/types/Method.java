package dev.selimaj.session.types;

import com.fasterxml.jackson.databind.JsonNode;

public interface Method<Req extends JsonNode, Res extends JsonNode, Err extends JsonNode> {
    String NAME = "";
}
