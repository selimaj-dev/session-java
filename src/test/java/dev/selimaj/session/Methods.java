package dev.selimaj.session;

import com.fasterxml.jackson.databind.node.TextNode;

import dev.selimaj.session.types.Method;

public class Methods {
    public static class Data extends Method<TextNode, TextNode, TextNode> {
        Data() {
            super("data", TextNode.class, TextNode.class, TextNode.class);
        }
    }
}
