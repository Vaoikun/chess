package server;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;


public class Connection {
    public Session session;
    public String authToken;
    public Connection(String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
    }
    public void send(String massage) throws IOException {
        session.getRemote().sendString(massage);
    }
}
