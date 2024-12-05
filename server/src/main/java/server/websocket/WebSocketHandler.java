package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.LoadGame;

import java.io.IOException;
import java.util.Vector;


@WebSocket
public class WebSocketHandler {
    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    public enum KEYITEMS {join, observe, move, leave, resign, check, checkmate}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType()) {
            case UserGameCommand.CommandType.CONNECT -> observeOrJoin(message, session);
            case UserGameCommand.CommandType.LEAVE -> leave(message, session);
            case UserGameCommand.CommandType.MAKE_MOVE -> movePiece(message, session);
            case UserGameCommand.CommandType.RESIGN -> resign(message, session);
        }
    }

    public static void sendingErrorMessage(Session session, String errorJson) throws IOException {
        Connection connection = new Connection(null, session);
        if (connection.session.isOpen()) {
            if (connection.session.equals(session)) {
                connection.send(errorJson);
            }
        }
    }

    public static void sendingLoadGameToAllOthers(String authToken, LoadGame loadGame, int gameID) throws IOException {
        Vector<Connection> smallGame = ConnectionManager.CONNECTION.get(gameID);
        for (Connection connection : smallGame) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(authToken)) {
                    Gson gson = new Gson();
                    String loadGameJson = gson.toJson(loadGame);
                    connection.send(loadGameJson);
                }
            }
        }
    }

    public static void sendingLoadGame(String authToken, LoadGame loadGame, int gameID) throws IOException {
        Vector<Connection> smallGame = ConnectionManager.CONNECTION.get(gameID);
        Vector<Connection> removeList = new Vector<>();
        for (Connection connection : smallGame) {
            if (connection.session.isOpen()) {
                if (connection.authToken.equals(authToken)) {
                    Gson gson = new Gson();
                    String loadGameJson = gson.toJson(loadGame);
                    connection.send(loadGameJson);
                }
            } else {
                removeList.add(connection);
            }
        }
    }

    public static void observeOrJoin(String message, Session session){
        Gson gson = new Gson();
    };

    public static void leave(String message, Session session){return;}

    public static void movePiece(String message, Session session){return;}

    public static void resign(String message, Session session){return;}
}
