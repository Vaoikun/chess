package server.websocket;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.commands.websocketrequest.Connect;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.Error;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();
    public enum KEYITEMS {join, observe, makeMove, leave, resign, check, checkMake}

    @OnWebSocketMessage
    public void onWebSocketMessage(Session session, String message) {
        try {
            Gson json = new Gson();
            UserGameCommand userGameCommand = json.fromJson(message, UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT ->
            }
        }
    }

    public static void join (Session session, String message) {
        Gson json = new Gson();
        Connect connect = json.fromJson(message, Connect.class);
        try {
            GameData gameData = null;
            String authToken = connect.getAuthToken();
            SQLAuthDAO authDB = new SQLAuthDAO();
            SQLGameDAO gameDB = new SQLGameDAO();
            int gameID = connect.getGameID();
            String username = authDB.getUsername(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setMessage("Error: unauthorized.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            try {
                gameData = gameDB.getGame(gameID);
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setMessage("Error: game not found.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            if (gameData != null && username != null) {
                CONNECTION_MANAGER.add(session, )
            }
        }
    }

    public static void sendErrorMessage (Session session, String errorMessage) throws IOException {
        Connection connection = new Connection(session, null);
        if (connection.session.isOpen()) {
            if (connection.session.equals(session)) {
                connection.send(errorMessage);
            }
        }
    }
}
