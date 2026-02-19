package dev.selimaj.session;

import com.fasterxml.jackson.databind.node.TextNode;

import dev.selimaj.session.types.Method;

public class Methods {
    public static final Method<TextNode, TextNode, TextNode> Data = new Method<>("data",
            TextNode.class, TextNode.class, TextNode.class);
}
