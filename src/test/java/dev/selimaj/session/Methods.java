package dev.selimaj.session;

import com.fasterxml.jackson.databind.node.TextNode;

import dev.selimaj.session.types.Method;

public class Methods {
    public static class Data implements Method<TextNode, TextNode, TextNode> {
        @Override
        public String getName() {
            return "data";
        }

        public static String getNameStatic() {
            return "data";
        }
    }
}
