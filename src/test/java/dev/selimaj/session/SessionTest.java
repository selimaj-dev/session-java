package dev.selimaj.session;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.TextNode;

public class SessionTest {
    static void testRequest() throws Exception {
        Session session = Session.connect("ws://localhost:8080/");

        TextNode response = session.request(Methods.Data.class, TextNode.valueOf("Hello from client")).get();

        System.out.println(response);

        Thread.sleep(1000);

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
