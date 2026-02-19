package dev.selimaj.session;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.TextNode;
import dev.selimaj.session.types.SessionResult;

public class SessionTest {
    static void testRequest() throws Exception {
        Session session = Session.connect("ws://localhost:8080/");

        session.onRequest(Methods.Data, (id, data) -> {
            return SessionResult.ok(TextNode.valueOf("Response from client"));
        });

        TextNode response = session.request(Methods.Data, TextNode.valueOf("Hello from client")).get();

        System.out.println(response);

        session.close();
    }

    @Test
    void requestTest() throws Exception {
        testRequest();
    }

    public static void main(String[] args) {
        try {
            testRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
