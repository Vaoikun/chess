package server.websocket;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import javax.management.Notification;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final static ConcurrentHashMap<Integer, Vector<Connection>> CONNECTION = new ConcurrentHashMap<Integer, Vector<Connection>>();

    public void add(String authToken, Session session, Integer gameID){
        var connection = new Connection(authToken, session);
        if(CONNECTION.get(gameID) == null){
            var newGame = new Vector<Connection>();
            CONNECTION.put(gameID, newGame);
            newGame.add(connection);
        }else{
            var game = CONNECTION.get(gameID);
            game.add(connection);
        }
    }

    public void remove(Integer gameID, String authToken){
        if(CONNECTION.get(gameID) == null){
            return;
        }else{
            var game = CONNECTION.get(gameID);
            for(int i = game.size() - 1; i >= 0; i--){
                if(Objects.equals(game.get(i).authToken, authToken)){
                    game.remove(i);
                }
            }
        }
    }

    public void broadcast(int gameID, Session session, String notification) throws IOException {
        var removeList = new Vector<Connection>();
        if (CONNECTION.get(gameID) != null) {
            Vector<Connection> games = CONNECTION.get(gameID);
            for (Connection connection : games) {
                if(connection.session.isOpen()){
                    if(!connection.session.equals(session)){
                        connection.send(notification);
                    }
                }else{
                    removeList.add(connection);
                }
            }
        }
    }
}
