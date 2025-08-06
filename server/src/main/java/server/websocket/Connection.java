package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public Session session;
    public String authToken;

    public Connection (Session session, String authToken) {
        this.session = session;
        this.authToken = authToken;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
