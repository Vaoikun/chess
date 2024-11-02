package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final static ConcurrentHashMap<Integer, Vector<Connection>> CONNECTIONS = new ConcurrentHashMap<>();

    public void add(String authToken, Session session, Integer gameID) {
        var connection = new Connection(authToken, session);
        if (CONNECTIONS.get(gameID) != null) {
            var smallGame = CONNECTIONS.get(gameID);
            smallGame.add(connection);
        } else {
            var smallGame = new Vector<Connection>();
            CONNECTIONS.put(gameID, smallGame);
            smallGame.add(connection);
        }
    }

    public void remove(int gameID, String authToken) {
        if (CONNECTIONS.get(gameID) != null) {
            var smallGame = CONNECTIONS.get(gameID);
            for (int i = smallGame.size() -1; i >= 0; i-- ) {
                if (Objects.equals(smallGame.get(i).authToken, authToken)) {
                    smallGame.remove(i);
                }
            }
        }
    }

    public void broadcast(int gameID, Session senderSession, String notification) throws IOException {
        var removeList = new Vector<Connection>();
        if (CONNECTIONS.get(gameID) != null) {
            Vector<Connection> smallGame = CONNECTIONS.get(gameID);
            for (Connection connection : smallGame) {
                if (connection.session.isOpen()) {
                    if (!connection.session.equals(senderSession)) {
                        connection.send(notification);
                    }
                } else {
                    removeList.add(connection);
                }
            }
        }
    }
}