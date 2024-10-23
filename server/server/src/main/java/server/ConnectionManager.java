package server;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    //for big websocket space, ConcurrentHashMap is better than hashmap
    public final static ConcurrentHashMap<Integer, Vector<Connection>> CONNECTIONS = new ConcurrentHashMap<>();
    public void add(String authToken, Session session, Integer gameID) {
        var connct = new Connection(authToken, session);
        if (CONNECTIONS.get(gameID) == null) {
            var smallGame = new Vector<Connection>();
            CONNECTIONS.put(gameID, smallGame);
            smallGame.add(connct);
        } else {
            var smallGame = CONNECTIONS.get(gameID);
            smallGame.add(connct);
        }
    }

    public void remove(int gameID, String authToken) {
        if (CONNECTIONS.get(gameID) != null) {
            var smallGames = CONNECTIONS.get(gameID);
            for (int i = smallGames.size() -1; i >= 0; i-- ) {
                if (Objects.equals(smallGames.get(i).authToken, authToken)) { // remove if authToken matches
                    smallGames.remove(i);
                }
            }
        }
    }

    public void broadcast(int gameID, Session senderSession, String notification) throws IOException {
        var removeList = new Vector<Connection>();
        if (CONNECTIONS.get(gameID) != null) {
            Vector<Connection> smallGames = CONNECTIONS.get(gameID);
            for (Connection connection : smallGames) {
                if (connection.session.isOpen()) {
                    if (!connection.session.equals(senderSession)) {
                        connection.send(notification);
                    }
                }else{
                    removeList.add(connection);
                }
            }
        }
    }
}
