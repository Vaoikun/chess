package server.websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.commands.websocketrequest.*;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.LoadGame;
import websocket.messages.websocketresponse.Error;
import dataaccess.*;
import websocket.messages.websocketresponse.Notification;

import java.io.IOException;
import java.sql.SQLException;
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
        Connect connect = gson.fromJson(message, Connect.class);
        try{
            GameData game = null;
            String authToken = connect.getAuthToken();
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
            SQLGameDAO sqlGame = new SQLGameDAO();
            int gameID = connect.getGameID();
            String username = sqlAuth.getAuth(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try {
                game = sqlGame.getGame(gameID);
            }catch (DataAccessException e){
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Database error: game is not found.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if (game != null && username != null) {
                CONNECTION_MANAGER.add(authToken ,session, gameID);
                if (username.equals(game.whiteUsername())){
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " is joining the game with white color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                } else if (username.equals(game.blackUsername())){
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(username + " is joining the game with black color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                }else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, null);
                    notification.setMessage(username + " is observing the game.");String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID,session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                }
            }
        }catch (DataAccessException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void leave(String message, Session session){
        Gson gson = new Gson();
        Leave leave = gson.fromJson(message, Leave.class);
        try{
            String authToken = leave.getAuthToken();
            int gameID = leave.getGameID();
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
            SQLGameDAO sqlGame = new SQLGameDAO();
            String username = sqlAuth.getAuth(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }else{
                GameData currentGame = sqlGame.getGame(gameID);
                if (username.equals(currentGame.whiteUsername())){
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " is leaving the game with white color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);
                    sqlGame.updateGame(null, ChessGame.TeamColor.WHITE, currentGame);
                } else if (username.equals(currentGame.blackUsername())){
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(username + " is leaving the game with black color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);
                    sqlGame.updateGame(null, ChessGame.TeamColor.BLACK, currentGame);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, null);
                    notification.setMessage(username + " is leaving the game as observer.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);
                }
            }
        } catch (DataAccessException | IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void movePiece(String message, Session session){return;}

    public static void resign(String message, Session session){return;}
}
