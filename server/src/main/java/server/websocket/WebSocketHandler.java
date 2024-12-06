package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
import java.util.Collection;
import java.util.Objects;
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

    public static void sendingGameSetNotificationToAllObservers(String authToken, int gameID, Notification notification) throws IOException, DataAccessException {
        Vector<Connection> smallGame = ConnectionManager.CONNECTION.get(gameID);
        SQLAuthDAO sqlAuth = new SQLAuthDAO();
        SQLGameDAO sqlGame = new SQLGameDAO();
        for (Connection connection : smallGame) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(authToken)) {
                    String username = sqlAuth.getAuth(authToken);
                    GameData chessGame = sqlGame.getGame(gameID);
                    if (!username.equals(chessGame.whiteUsername()) && !username.equals(chessGame.blackUsername())) {
                        Gson gson = new Gson();
                        String messageJson = gson.toJson(notification);
                        connection.send(messageJson);
                    }
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

    public static void movePiece(String message, Session session){
        try {
            GameData currentGame = null;
            Gson gson = new Gson();
            SQLGameDAO sqlGame = new SQLGameDAO();
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
            MakeMove makeMove = gson.fromJson(message, MakeMove.class);
            int gameID = makeMove.getGameID();
            String authToken = makeMove.getAuthToken();
            String username = sqlAuth.getAuth(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try {
                currentGame = sqlGame.getGame(gameID);
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is not found.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if ( currentGame != null && username != null) {
                ChessMove chessMove = makeMove.getMove();
                ChessGame chessGame = currentGame.game();
                Collection<ChessMove> validMoves = chessGame.validMoves(chessMove.getStartPosition());
                if (validMoves.contains(chessMove)) {
                    if (username.equals(currentGame.blackUsername())) {
                        blackCheckmateStalemateChecker(chessGame, chessMove, sqlGame, gameID, currentGame, username, authToken, gson, session);
                    } else if (username.equals(currentGame.whiteUsername())) {
                        whiteCheckmateStalemateChecker(chessGame, chessMove, sqlGame, gameID, currentGame, username, authToken, gson, session);
                    } else {
                        sendError("Observer cannot make move.", session, gson);
                    }
                } else {
                    sendError("The move is not valid.", session, gson);
                }
            }
        } catch (DataAccessException | IOException | InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendError(String errorMessage, Session session, Gson gson) throws IOException {
        Error error = new Error(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(errorMessage);
        String errorJson = gson.toJson(error);
        sendingErrorMessage(session, errorJson);
    }

    public static void sendNotification(String username, int gameID, String message, Gson gson, ChessGame.TeamColor color) throws IOException {
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, color);
        notification.setMessage(message);
        String messageJson = gson.toJson(notification);
        CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
    }

    public static void blackCheckmateStalemateChecker(ChessGame chessGame, ChessMove chessMove, SQLGameDAO sqlGame,
                                                 int gameID, GameData currentGame, String username, String authToken, Gson gson, Session session) throws IOException, InvalidMoveException, DataAccessException {

        if (chessGame.turnColor == ChessGame.TeamColor.BLACK) {
            if (!chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) && !chessGame.isInStalemate(ChessGame.TeamColor.BLACK) && !chessGame.isResigned) {
                chessGame.makeMove(chessMove);
                sqlGame.updateChessGame(chessGame, gameID);
                if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.blackUsername() + " is in stalemate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    sendingGameSetNotificationToAllObservers(authToken, gameID, notification);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.blackUsername() + " is in checkmate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    sendingGameSetNotificationToAllObservers(authToken, gameID, notification);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.whiteUsername() + " is in check.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                    sendingGameSetNotificationToAllObservers(authToken, gameID, notification);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(username + " is making move from " + chessMove.getStartPosition() + " to " + chessMove.getEndPosition());
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame, gameID);
                }
            } else {
                sendError("You cannot make move after the game is over.", session, gson);
            }
        } else {
            sendError("It's not your turn yet. Please wait your opponent to finish the move.", session, gson);
        }
    }

    public static void whiteCheckmateStalemateChecker(ChessGame chessGame, ChessMove chessMove, SQLGameDAO sqlGame,
                                                      int gameID, GameData currentGame, String username, String authToken, Gson gson, Session session) throws IOException, InvalidMoveException {
        if (chessGame.turnColor == ChessGame.TeamColor.WHITE) {
            if (!chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) && !chessGame.isInStalemate(ChessGame.TeamColor.WHITE) && !chessGame.isResigned) {
                chessGame.makeMove(chessMove);
                sqlGame.updateChessGame(chessGame, gameID);
                if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in stalemate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    Connection connectionMover = new Connection(authToken, session);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in checkmate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    Connection connectionMover = new Connection(authToken, session);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in check.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    Connection connectionMover = new Connection(authToken, session);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " is making move from " + chessMove.getStartPosition() + " to " + chessMove.getEndPosition());
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendingLoadGame(authToken, loadGame, gameID);
                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                }
            } else {
                sendError("You cannot make move after the game is over.", session, gson);
            }
        } else {
            sendError("It's not your turn yet. Please wait your opponent to finish the move.", session, gson);
        }
    }

    public static void resign(String message, Session session) {
        try {
            GameData gameData = null;
            ChessGame chessGame = null;
            Gson gson = new Gson();
            Resign resign = gson.fromJson(message, Resign.class);
            int gameID = resign.getGameID();
            String authToken = resign.getAuthToken();
            SQLGameDAO sqlGame = new SQLGameDAO();
            SQLUserDAO sqlUser = new SQLUserDAO();
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
            String username = sqlAuth.getAuth(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try {
                gameData = sqlGame.getGame(gameID);
                chessGame = gameData.game();
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is not found.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if (username != null && gameData != null) {
                if (username.equals(gameData.whiteUsername())) {
                    if (!chessGame.isResigned) {
                        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                        notification.setMessage(username + " resigns the game.");
                        String messageJson = gson.toJson(notification);
                        CONNECTION_MANAGER.broadcast(gameID, session, messageJson); // send to everyone else
                        Connection resignMaker = new Connection(authToken, session);
                        if (resignMaker.session.isOpen()) {
                            resignMaker.send(messageJson);
                        }
                        chessGame.isResigned = true;
                        sqlGame.updateChessGame(chessGame, gameID);
                    } else {
                        Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                        error.setErrorMessage("You cannot resign after one player already resigned.");
                        String errorJson = gson.toJson(error);
                        sendingErrorMessage(session, errorJson);
                    }
                } else if (username.equals(gameData.blackUsername())) {
                    if (!chessGame.isResigned) {
                        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                        notification.setMessage(username + " resigns the game.");
                        String messageJson = gson.toJson(notification);
                        CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                        chessGame.isResigned = true;
                        sqlGame.updateChessGame(chessGame, gameID);
                    } else {
                        Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                        error.setErrorMessage("You cannot resign after one player already resigned.");
                        String errorJson = gson.toJson(error);
                        sendingErrorMessage(session, errorJson);
                    }
                } else {
                    Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Observer cannot resign.");
                    String errorJson = gson.toJson(error);
                    sendingErrorMessage(session, errorJson);
                }
            }
        } catch (DataAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
