package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ServerException;
import websocket.commands.UserGameCommand;
import websocket.commands.websocketrequest.Connect;
import websocket.commands.websocketrequest.MakeMove;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.Error;
import websocket.messages.websocketresponse.LoadGame;
import websocket.messages.websocketresponse.Notification;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

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
                case UserGameCommand.CommandType.CONNECT -> join(session, message);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            } catch (DataAccessException | ServerException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setMessage("Error: game not found.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            if (gameData != null && username != null) {
                CONNECTION_MANAGER.add(session, gameID, authToken);
                if (username.equals(gameData.whiteUsername())) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " has joined the game as white team.");
                    String notificationMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, notificationMessage);
                    ChessGame chessGame = gameData.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                } else if (username.equals(gameData.blackUsername())) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " has joined the game as white team.");
                    String notificationMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, notificationMessage);
                    ChessGame chessGame = gameData.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, null);
                    notification.setMessage(username + "left the game.");
                    String notificationMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, notificationMessage);
                    CONNECTION_MANAGER.remove(authToken, gameID);
                }
            }
        } catch (DataAccessException | IOException | ServerException e) {
            throw new RuntimeException(e.getMessage());
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

    public static void sendLoadGameMessage (LoadGame loadGame, String authToken, int gameID) throws IOException {
        Vector<Connection> gameList = ConnectionManager.CONNECTION.get(gameID);
        Vector<Connection> junkList = new Vector<>();
        for (Connection connection : gameList) {
            if (connection.session.isOpen()) {
                if (connection.authToken.equals(authToken)) {
                    Gson json = new Gson();
                    String loadGameJson = json.toJson(loadGame);
                    connection.send(loadGameJson);
                }
            } else {
                junkList.add(connection);
            }
        }
    }

    public static void movePiece(Session session, String message) {
        try {
            GameData gameData = null;
            Gson json = new Gson();
            SQLGameDAO gameDB = new SQLGameDAO();
            SQLAuthDAO authDB = new SQLAuthDAO();
            MakeMove makeMove = json.fromJson(message, MakeMove.class);
            int gameID = makeMove.getGameID();
            String authToken = makeMove.getAuthToken();
            String username = authDB.getUsername(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setMessage("Unauthorized.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            try {
                gameData = gameDB.getGame(gameID);
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setMessage("Error: Failed to find game.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, message);
            }
            if (gameData != null && username != null) {
                ChessMove chessMove = makeMove.getChessMove();
                ChessGame chessGame = gameData.game();
                Collection<ChessMove> legalMoves = chessGame.validMoves(chessMove.getStartPosition());
                if (legalMoves.contains(chessMove)) {
                    if (username.equals(gameData.blackUsername())) {
                        checkmateChecker(session, json, authToken, username, gameID, chessGame, chessMove, gameDB, gameData, ChessGame.TeamColor.BLACK, ChessGame.TeamColor.WHITE);
                    }
                }
            }
        } catch (DataAccessException | IOException | ServerException | InvalidMoveException e) {

        }
    }

    private static void checkmateChecker(Session session, Gson json, String authToken,
                                              String username, int gameID, ChessGame chessGame,
                                              ChessMove chessMove, SQLGameDAO gameDB, GameData gameData,
                                         ChessGame.TeamColor teamColor, ChessGame.TeamColor oppositeTeam)
            throws IOException, InvalidMoveException, DataAccessException, ServerException {
        if (chessGame.isInCheckmate(teamColor) && !chessGame.isInStalemate(teamColor) && !chessGame.isResigned) {
            chessGame.makeMove(chessMove);
            gameDB.chessGameUpdate(chessGame, gameID);
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, teamColor);
            notification.setMessage(username + " made move from" + coordinateConverter(chessMove.getStartPosition()) +
                    "to" + coordinateConverter(chessMove.getEndPosition()));
            String moveMessage = json.toJson(notification);
            CONNECTION_MANAGER.broadcast(session, gameID, moveMessage);
            if (chessGame.isInStalemate(oppositeTeam)) {
                Notification staleNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, teamColor);
                staleNotification.setMessage(gameData.blackUsername() + " is in stalemate.");
                String staleMessage = json.toJson(staleNotification);
                CONNECTION_MANAGER.broadcast(session, gameID, staleMessage);
                Connection connection = new Connection(session, authToken);
                if (connection.session.isOpen()) {
                    connection.send(staleMessage);
                }
                sendGameSetMessage(notification, gameID, authToken);
                LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                sendLoadGameMessage(loadGame, authToken, gameID);
                broadcastLoadGameMessage(loadGame, authToken, gameID);
            }
        }
    }

    private static String coordinateConverter (ChessPosition chessPosition) {
        String coordinate;
        int column = chessPosition.getColumn();
        int row = chessPosition.getRow();
        switch (column) {
            case 1 -> coordinate = "a" + row;
            case 2 -> coordinate = "b" + row;
            case 3 -> coordinate = "c" + row;
            case 4 -> coordinate = "d" + row;
            case 5 -> coordinate = "e" + row;
            case 6 -> coordinate = "f" + row;
            case 7 -> coordinate = "g" + row;
            case 8 -> coordinate = "h" + row;
            default -> coordinate = "";
        }
        return coordinate;
    }

    public static void sendGameSetMessage (Notification notification, int gameID, String authToken)
            throws IOException, DataAccessException, ServerException {
        Vector<Connection> gameList = ConnectionManager.CONNECTION.get(gameID);
        SQLGameDAO gameDB = new SQLGameDAO();
        SQLAuthDAO authDB = new SQLAuthDAO();
        for (Connection connection : gameList) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(authToken)) {
                    String username = authDB.getUsername(authToken);
                    GameData chessGame = gameDB.getGame(gameID);
                    if (!username.equals(chessGame.whiteUsername()) && !username.equals(chessGame.blackUsername())) {
                        Gson json = new Gson();
                        String message = json.toJson(notification);
                        connection.send(message);
                    }
                }
            }
        }
    }

    public static void broadcastLoadGameMessage (LoadGame loadGame, String authToken, int gameID) throws IOException {
        Vector<Connection> gameList = ConnectionManager.CONNECTION.get(gameID);
        for (Connection connection : gameList) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(authToken)) {
                    Gson json = new Gson();
                    String loadGameMessage = json.toJson(loadGame);
                    connection.send(loadGameMessage);
                }
            }
        }
    }

}
