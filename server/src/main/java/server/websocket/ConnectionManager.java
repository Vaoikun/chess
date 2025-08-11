package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final static ConcurrentHashMap<Integer, Vector<Connection>> CONNECTION = new ConcurrentHashMap<>();

    public void broadcast(Session session, int gameID, String message) throws IOException {
        var connectionList = new Vector<Connection>();
        if (CONNECTION.get(gameID) != null) {
            Vector<Connection> gameList = CONNECTION.get(gameID);
            for (Connection connection : gameList) {
                if (connection.session.isOpen()) {
                    if (!connection.session.equals(session)) {
                        connection.send(message);
                    }
                } else {
                    connectionList.add(connection);
                }
            }
        }
    }

    public void add(Session session, int gameID, String authToken) {
        var connection = new Connection(session, authToken);
        if (CONNECTION.get(gameID) == null) {
            var connectionList = new Vector<Connection>();
            CONNECTION.put(gameID, connectionList);
            connectionList.add(connection);
        } else {
            var gameList = CONNECTION.get(gameID);
            gameList.add(connection);
        }
    }

    public void remove(String authToken, int gameID) {
        if (CONNECTION.get(gameID) == null) {
            return;
        } else {
            var gameList = CONNECTION.get(gameID);
            for (int i = gameList.size() - 1; i >= 0; i--) {
                if (Objects.equals(gameList.get(i).authToken, authToken)) {
                    gameList.remove(i);
                }
            }
        }
    }
}
