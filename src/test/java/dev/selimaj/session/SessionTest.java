package dev.selimaj.session;

import org.junit.jupiter.api.Test;

public class SessionTest {
    static void testRequest() throws Exception {
        Session session = Session.connect("ws://localhost:8080/");

        String response = session.request(Methods.Data.getNameStatic(), "Hello from client", String.class).get();

        System.out.println(response);

        Thread.sleep(1000);
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
