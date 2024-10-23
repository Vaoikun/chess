package server;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class MyConnectionManager
{
    // big websocket space. ConcurrentHashMap is better than hashmap
    public final static ConcurrentHashMap<Integer, Vector<Connection>> CONNECTIONS = new ConcurrentHashMap<>();

    public void add(String authToken, Session session, Integer gameID)
    {
        var connection = new Connection(authToken, session);
        if (CONNECTIONS.get(gameID) != null)
        {
            var smallGame = CONNECTIONS.get(gameID);
            smallGame.add(connection); // add into the game which matches the gameID
        }
        else
        {
            var smallGame = new Vector<Connection>();
            CONNECTIONS.put(gameID, smallGame);
            smallGame.add(connection);
        }
    }

    public void remove(int gameID, String authToken)
    {
        if (CONNECTIONS.get(gameID) != null) // means the game is exist
        {
            var smallGame = CONNECTIONS.get(gameID);
            for (int i = smallGame.size() -1; i >= 0; i-- )
            {
                if (Objects.equals(smallGame.get(i).authToken, authToken))
                {
                    smallGame.remove(i); // delete it
                }
            }
        }
    }

    public void broadcast(int gameID, Session senderSession, String notification) throws IOException {
        var removeList = new Vector<Connection>();
        if (CONNECTIONS.get(gameID) != null)
        {
            Vector<Connection> smallGame = CONNECTIONS.get(gameID);
            for (Connection connection : smallGame)
            {
                if (connection.session.isOpen())
                {
                    if (!connection.session.equals(senderSession))
                    {
                        connection.send(notification);
                    }
                }
                else
                {
                    removeList.add(connection);
                }
            }
        }

    }
}
