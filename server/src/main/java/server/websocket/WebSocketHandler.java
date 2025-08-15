package server.websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLAuthDAO;
import dataaccess.SQLGameDAO;
import dataaccess.SQLUserDAO;
import httpresponse.MessageResponse;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import server.ServerException;
import websocket.commands.UserGameCommand;
import websocket.commands.websocketrequest.Connect;
import websocket.commands.websocketrequest.Leave;
import websocket.commands.websocketrequest.MakeMove;
import websocket.commands.websocketrequest.Resign;
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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        try {
            Gson json = new Gson();
            UserGameCommand userGameCommand = json.fromJson(message, UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT -> join(session, message);
                case UserGameCommand.CommandType.MAKE_MOVE -> movePiece(session, message);
                case UserGameCommand.CommandType.LEAVE -> leave(session, message);
                case UserGameCommand.CommandType.RESIGN -> resign(session, message);
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
                error.setErrorMessage("Error: unauthorized.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            try {
                gameData = gameDB.getGame(gameID);
            } catch (DataAccessException | ServerException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: game not found.");
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
                    notification.setMessage(username + "is observing the game.");
                    String notificationMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, notificationMessage);
                    ChessGame chessGame = gameData.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
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

    public static void movePiece(Session session, String message){
        try {
            GameData currentGame = null;
            Gson gson = new Gson();
            SQLGameDAO sqlGame = new SQLGameDAO();
            SQLAuthDAO sqlAuth = new SQLAuthDAO();
            MakeMove makeMove = gson.fromJson(message, MakeMove.class);
            int gameID = makeMove.getGameID();
            String authToken = makeMove.getAuthToken();
            String username = sqlAuth.getUsername(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendErrorMessage(session, errorJson);
            }
            try {
                currentGame = sqlGame.getGame(gameID);
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is not found.");
                String errorJson = gson.toJson(error);
                sendErrorMessage(session, errorJson);
            }
            if ( currentGame != null && username != null) {
                ChessMove chessMove = makeMove.getChessMove();
                ChessGame chessGame = currentGame.game();
                Collection<ChessMove> validMoves = chessGame.validMoves(chessMove.getStartPosition());
                if (validMoves.contains(chessMove)) {
                    if (username.equals(currentGame.blackUsername())) {
                        blackCheckmateStalemateChecker(chessGame, chessMove, sqlGame, gameID, currentGame, username, authToken, gson, session);
                    } else if (username.equals(currentGame.whiteUsername())) {
                        whiteCheckmateStalemateChecker(chessGame, chessMove, sqlGame, gameID, currentGame, username, authToken, gson, session);
                    } else {
                        sendErrorMessage(session, "Observer cannot make move.");
                    }
                } else {
                    sendErrorMessage(session,"The move is not valid.");
                }
            }
        } catch (DataAccessException | IOException | InvalidMoveException | ServerException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void blackCheckmateStalemateChecker(ChessGame chessGame, ChessMove chessMove, SQLGameDAO sqlGame, int gameID,
                                                      GameData currentGame, String username, String authToken, Gson gson, Session session)
            throws IOException, InvalidMoveException, DataAccessException, ServerException {

        if (chessGame.teamColor == ChessGame.TeamColor.BLACK) {
            if (!chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) && !chessGame.isInStalemate(ChessGame.TeamColor.BLACK) && !chessGame.isResigned) {
                chessGame.makeMove(chessMove);
                sqlGame.chessGameUpdate(chessGame, gameID);
                Notification moveNotification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                moveNotification.setMessage(username + " made move from " + coordinateConverter(chessMove.getStartPosition()) + " to " + coordinateConverter(chessMove.getEndPosition()));
                String moveMessageJson = gson.toJson(moveNotification);
                CONNECTION_MANAGER.broadcast(session, gameID, moveMessageJson);
                if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.blackUsername() + " is in stalemate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);
                    sendGameSetMessage(notification, gameID, authToken);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.blackUsername() + " is in checkmate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);
                    sendGameSetMessage(notification, gameID, authToken);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else if (chessGame.isInCheck(ChessGame.TeamColor.WHITE)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(currentGame.whiteUsername() + " is in check.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(null, gameID, messageJson);
                    sendGameSetMessage(notification, gameID, authToken);
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else {
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken, gameID);
                }
            } else {
                sendErrorMessage(session, "You cannot make move after the game is over.");
            }
        } else {
            sendErrorMessage(session, "It's not your turn yet. Please wait your opponent to finish the move.");
        }
    }

    public static void whiteCheckmateStalemateChecker(ChessGame chessGame, ChessMove chessMove, SQLGameDAO sqlGame, int gameID,
                                                      GameData currentGame, String username, String authToken, Gson gson, Session session)
            throws IOException, InvalidMoveException {
        if (chessGame.teamColor == ChessGame.TeamColor.WHITE) {
            if (!chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) && !chessGame.isInStalemate(ChessGame.TeamColor.WHITE) && !chessGame.isResigned) {
                chessGame.makeMove(chessMove);
                sqlGame.chessGameUpdate(chessGame, gameID);
                if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in stalemate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);
                    Connection connectionMover = new Connection(session, authToken);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in checkmate.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);
                    Connection connectionMover = new Connection(session, authToken);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else if (chessGame.isInCheck(ChessGame.TeamColor.BLACK)) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(currentGame.blackUsername() + " is in check.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);
                    Connection connectionMover = new Connection(session, authToken);
                    if (connectionMover.session.isOpen()) {
                        connectionMover.send(messageJson);
                    }
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " made move from " + coordinateConverter(chessMove.getStartPosition()) + " to " + coordinateConverter(chessMove.getEndPosition()));
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, messageJson);LoadGame loadGame
                            = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                    sendLoadGameMessage(loadGame, authToken, gameID);
                    broadcastLoadGameMessage(loadGame, authToken , gameID);
                }
            } else {
                sendErrorMessage(session, "You cannot make move after the game is over.");
            }
        } else {
            sendErrorMessage(session, "It's not your turn yet. Please wait your opponent to finish the move.");
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

    private static String usernameSwitcher (GameData gameData, ChessGame.TeamColor teamColor) {
        if (ChessGame.TeamColor.WHITE == teamColor) {
            return gameData.whiteUsername();
        } else if (ChessGame.TeamColor.BLACK == teamColor) {
            return gameData.blackUsername();
        }
        return null;
    }

    private static void postErrorMessage (Session session, String message, Gson json) throws IOException {
        Error error = new Error(ServerMessage.ServerMessageType.ERROR);
        error.setErrorMessage(message);
        String errorMassage = json.toJson(error);
        sendErrorMessage(session, errorMassage);
    }

    public static void leave (Session session, String message) {
        try {
            Gson json = new Gson();
            Leave leave = json.fromJson(message, Leave.class);
            SQLGameDAO gameDB = new SQLGameDAO();
            SQLAuthDAO authDB = new SQLAuthDAO();
            String authToken = leave.getAuthToken();
            int gameID = leave.getGameID();
            String username = authDB.getUsername(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: unauthorized.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            } else {
                GameData gameData = gameDB.getGame(gameID);
                if (username.equals(gameData.whiteUsername())) {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + "(white team) left the game.");
                    String leaveMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, leaveMessage);
                    CONNECTION_MANAGER.remove(authToken, gameID);
                    gameDB.updateGame(null, ChessGame.TeamColor.WHITE, gameData);
                } else if (username.equals(gameData.blackUsername())){
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + "(black team) left the game.");
                    String leaveMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, leaveMessage);
                    CONNECTION_MANAGER.remove(authToken, gameID);
                    gameDB.updateGame(null, ChessGame.TeamColor.BLACK, gameData);
                } else {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + "(observer) left the game.");
                    String leaveMessage = json.toJson(notification);
                    CONNECTION_MANAGER.broadcast(session, gameID, leaveMessage);
                    CONNECTION_MANAGER.remove(authToken, gameID);
                }
            }
        } catch (DataAccessException | IOException | ServerException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void resign(Session session, String message) {
        try {
            ChessGame chessGame = null;
            GameData gameData = null;
            Gson json = new Gson();
            Resign resign = json.fromJson(message, Resign.class);
            int gameID = resign.getGameID();
            String authToken = resign.getAuthToken();
            SQLGameDAO gameDB = new SQLGameDAO();
            SQLAuthDAO authDB = new SQLAuthDAO();
            SQLUserDAO userDB = new SQLUserDAO();
            String username = authDB.getUsername(authToken);
            if (username == null) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: unauthorized.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            try {
                gameData = gameDB.getGame(gameID);
                chessGame = gameData.game();
            } catch (DataAccessException e) {
                Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Error: Game not found.");
                String errorMessage = json.toJson(error);
                sendErrorMessage(session, errorMessage);
            }
            if (username != null && gameData != null) {
                if (username.equals(gameData.blackUsername())) {
                    resignMessage(session, chessGame, authToken, username, gameID, json, ChessGame.TeamColor.BLACK, gameDB);
                } else if (username.equals(gameData.blackUsername())){
                    resignMessage(session, chessGame, authToken, username, gameID, json, ChessGame.TeamColor.WHITE, gameDB);
                } else {
                    Error error = new Error(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Error: Observer can't resign. Leave the game instead.");
                    String errorMessage = json.toJson(error);
                    sendErrorMessage(session, errorMessage);
                }
            }
        } catch (DataAccessException | IOException | ServerException e){
            throw new RuntimeException(e);
        }
    }

    private static void resignMessage (Session session, ChessGame chessGame, String authToken, String username,
                                int gameID, Gson json, ChessGame.TeamColor teamColor, SQLGameDAO gameDB) throws IOException {
        if (!chessGame.isResigned) {
            Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    username, teamColor);
            notification.setMessage(username + " resigned the game.");
            String resignMessage = json.toJson(notification);
            CONNECTION_MANAGER.broadcast(session, gameID, resignMessage);
            Connection connection = new Connection(session, authToken);
            if (connection.session.isOpen()) {
                connection.send(resignMessage);
            }
            chessGame.isResigned = true;
            gameDB.chessGameUpdate(chessGame, gameID);
        } else {
            Error error = new Error(ServerMessage.ServerMessageType.ERROR);
            error.setErrorMessage("Error: One player resigned already. You can leave the game.");
            String errorMessage = json.toJson(error);
            sendErrorMessage(session, errorMessage);
        }
    }
}
