package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.commands.websocketrequests.ConnectPlayer;
import websocket.commands.websocketrequests.Leave;
import websocket.commands.websocketrequests.MakeMove;
import websocket.commands.websocketrequests.Resign;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.ErrorWebsocket;
import websocket.messages.websocketresponse.LoadGame;
import websocket.messages.websocketresponse.Notification;

import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

@WebSocket
public class WebsocketHandler
{
    // websocket back-end. Receiving messages from websocket facade. return the messages from it to send back to websocketFacade.
    private static final MyConnectionManager CONNECTION_MANAGER = new MyConnectionManager();

    public enum KeyItems {join, observe, move, leave, resign, check, checkmate}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) // the message is just a websocketRequest, just make it as json to pass in.
    {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class); // make it to be userGameCommand

        switch (userGameCommand.getCommandType())
        {
            case UserGameCommand.CommandType.CONNECT -> observeOrJoin(message, session);
            case UserGameCommand.CommandType.LEAVE -> leave(message, session);
            case UserGameCommand.CommandType.MAKE_MOVE -> movePiece(message, session);
            case UserGameCommand.CommandType.RESIGN -> resign(message, session);
            // how about the check and checkmate?
        }
    }

    public static void sendingErrorMessage(Session session, String errorJson) throws IOException {
     Connection connection = new Connection(null, session);
     if (connection.session.isOpen())
     {
         if (connection.session.equals(session))
         {
             connection.send(errorJson);
         }
     }
    }

    // send to all others the loading game
    public static void sendingLoadGameToAllOthers(String authToken, LoadGame loadGame, int gameID) throws IOException {
        Vector<Connection> smallGame = MyConnectionManager.CONNECTIONS.get(gameID);
        for (Connection connection : smallGame)
        {
            if (connection.session.isOpen())
            {
                if (!connection.authToken.equals(authToken))
                {
                    Gson gson = new Gson();
                    String loadGameJson = gson.toJson(loadGame);
                    connection.send(loadGameJson);
                }
            }
        }
    }


    public static void sendingLoadGame(String authToken, LoadGame loadGame, int gameID) throws IOException {
        Vector<Connection> smallGame = MyConnectionManager.CONNECTIONS.get(gameID);
        Vector<Connection> removeList = new Vector<>();
        for (Connection connection : smallGame)
        {
            if (connection.session.isOpen())
            {
                if (connection.authToken.equals(authToken)) // only send to myself
                {
                    Gson gson = new Gson();
                    String loadGameJson = gson.toJson(loadGame);
                    connection.send(loadGameJson); // toString or toJson?
                }
            }
            else
            {
                removeList.add(connection);
            }
        }
    }
    public static void observeOrJoin(String message, Session session)
    {

        Gson gson = new Gson();
        ConnectPlayer connectPlayer = gson.fromJson(message, ConnectPlayer.class);
        try
        {
            GameData game = null;
            String authToken = connectPlayer.getAuthString();
            SQLAuth sqlAuth =  new SQLAuth();
            SQLGame sqlGame = new SQLGame();
            int gameID = connectPlayer.getGameID();
            String username = sqlAuth.getAuth(authToken);
            if (username == null)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try
            {
                game = sqlGame.getGame(gameID);
            }
            catch (DataAccessException e)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is not found.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if (game != null && username != null)
            {
                CONNECTION_MANAGER.add(authToken ,session, gameID);
                if (username.equals(game.whiteUsername()))
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                    notification.setMessage(username + " is joining the game with white color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                }
                else if (username.equals(game.blackUsername()))
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(username + " is joining the game with black color.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                }
                else
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, null);
                    notification.setMessage(username + " is observing the game.");String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID,session, messageJson);
                    ChessGame gameCurrent = game.game();
                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, gameCurrent);
                    sendingLoadGame(authToken, loadGame, gameID);
                }
            }

        } catch (DataAccessException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void leave(String message, Session session)
    {
        Gson gson = new Gson();
        Leave leave = gson.fromJson(message, Leave.class);
        try
        {
            String authToken = leave.getAuthString();
            int gameID = leave.getGameID();
            SQLAuth sqlAuth =  new SQLAuth();
            SQLGame sqlGame = new SQLGame();
            String username = sqlAuth.getAuth(authToken);
            if (username == null)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }

            else
            {
                GameData gameCurrent = sqlGame.getGame(gameID);

                if (username.equals(gameCurrent.whiteUsername()))
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);notification.setMessage(username + " is leaving the game.");
                    String messageJson = gson.toJson(notification);CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);
                    sqlGame.updateGame(null, ChessGame.TeamColor.WHITE, gameCurrent); // remove the user from game.
                }
                else if (username.equals(gameCurrent.blackUsername()))
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                    notification.setMessage(username + " is leaving the game.");
                    String messageJson = gson.toJson(notification);
                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);sqlGame.updateGame(null, ChessGame.TeamColor.BLACK, gameCurrent);

                }
                else // observer
                {
                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, null);
                    notification.setMessage(username + " is leaving the game.");
                    String messageJson = gson.toJson(notification);CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                    CONNECTION_MANAGER.remove(gameID, authToken);
                }
            }
        } catch (DataAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void movePiece(String message, Session session)
    {
        try
        {
            GameData gameCurrent = null;Gson gson = new Gson();
            SQLGame sqlGame = new SQLGame();
            SQLAuth sqlAuth = new SQLAuth();
            MakeMove makeMove = gson.fromJson(message, MakeMove.class);
            int gameID = makeMove.getGameID();
            String authToken = makeMove.getAuthString();
            String username = sqlAuth.getAuth(authToken);
            if (username == null)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try
            {
                gameCurrent = sqlGame.getGame(gameID);
            }
            catch (DataAccessException e)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);error.setErrorMessage("Game is not found.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if ( gameCurrent != null && username != null)
            {
                ChessMove chessMove = makeMove.getChessMove();ChessGame chessGame = gameCurrent.game();
                Collection<ChessMove> validMoves = chessGame.validMoves(chessMove.getStartPosition());
                if (validMoves.contains(chessMove))
                {
                    if (username.equals(gameCurrent.blackUsername()))
                    {
                        if (chessGame.turn == ChessGame.TeamColor.BLACK)
                        {
                            if (!chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) && !chessGame.isInStalemate(ChessGame.TeamColor.BLACK) && chessGame.isResigned != true)
                            {
                                chessGame.makeMove(chessMove);sqlGame.updateChessGame(chessGame, gameID);
                                chessGame.turn = ChessGame.TeamColor.WHITE;
                                if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                                    notification.setMessage(gameCurrent.whiteUsername() + " is in checkmate.");
                                    String messageJson = gson.toJson(notification);CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID);
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                                }
                                else if (chessGame.isInCheck(ChessGame.TeamColor.WHITE))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                                    notification.setMessage(gameCurrent.whiteUsername() + " is in check.");
                                    String messageJson = gson.toJson(notification);CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID);
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID);
                                }
                                else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                                    notification.setMessage(gameCurrent.whiteUsername() + " is in stalemate.");
                                    String messageJson = gson.toJson(notification);
                                    CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID); // send the updating game
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID); // send to others
                                }
                                else  // normal making move
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                                    notification.setMessage(username + " is making move from " + chessMove.getStartPosition() + " to " + chessMove.getEndPosition());
                                    String messageJson = gson.toJson(notification);CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID);sendingLoadGameToAllOthers(authToken, loadGame, gameID);
                                }
                            }
                            else // try to make move after game over
                            {
                                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                                error.setErrorMessage("You cannot make move after game over.");
                                String errorJson = gson.toJson(error);
                                sendingErrorMessage(session, errorJson);
                            }
                        }
                        else // not the user's turn, sending error
                        {
                            ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                            error.setErrorMessage("This is not your turn, cannot move.");
                            String errorJson = gson.toJson(error);
                            sendingErrorMessage(session, errorJson);
                        }
                    }
                    else if (username.equals(gameCurrent.whiteUsername())) // white user
                    {
                        if (chessGame.turn == ChessGame.TeamColor.WHITE)
                        {
                            if (!chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) && !chessGame.isInStalemate(ChessGame.TeamColor.WHITE) && chessGame.isResigned != true)
                            {
                                chessGame.makeMove(chessMove);sqlGame.updateChessGame(chessGame, gameID);
                                chessGame.turn = ChessGame.TeamColor.BLACK;
                                if (chessGame.isInCheck(ChessGame.TeamColor.BLACK))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                                    notification.setMessage(gameCurrent.blackUsername() + " is in check.");
                                    String messageJson = gson.toJson(notification);
                                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                                    Connection connectionMover = new Connection(authToken, session);
                                    if (connectionMover.session.isOpen())
                                    {
                                        connectionMover.send(messageJson);
                                    }
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID); // send the updating game
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID); // send to others
                                }
                                else if (chessGame.isInCheckmate(ChessGame.TeamColor.BLACK))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                                    notification.setMessage(gameCurrent.blackUsername() + " is in checkmate.");
                                    String messageJson = gson.toJson(notification);
                                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                                    Connection connectionMover = new Connection(authToken, session);
                                    if (connectionMover.session.isOpen())
                                    {
                                        connectionMover.send(messageJson);
                                    }
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID); // send the updating game
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID); // send to others
                                }
                                else if (chessGame.isInStalemate(ChessGame.TeamColor.BLACK))
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                                    notification.setMessage(gameCurrent.blackUsername() + " is in stalemate.");
                                    String messageJson = gson.toJson(notification);
                                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);
                                    Connection connectionMover = new Connection(authToken, session);
                                    if (connectionMover.session.isOpen())
                                    {
                                        connectionMover.send(messageJson);
                                    }
                                    LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID); // send the updating game
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID); // send to others
                                }
                                else
                                {
                                    Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                                    notification.setMessage(username + " is making move from " + chessMove.getStartPosition() + " to " + chessMove.getEndPosition());
                                    String messageJson = gson.toJson(notification);
                                    CONNECTION_MANAGER.broadcast(gameID, session, messageJson);LoadGame loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, chessGame);
                                    sendingLoadGame(authToken, loadGame, gameID); // send the updating game
                                    sendingLoadGameToAllOthers(authToken, loadGame , gameID); // send to other
                                }
                            }
                            else
                            {
                                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                                error.setErrorMessage("You cannot make move after game over.");
                                String errorJson = gson.toJson(error);
                                sendingErrorMessage(session, errorJson);}
                        }
                        else
                        {
                            ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                            error.setErrorMessage("This is not your turn, cannot move.");
                            String errorJson = gson.toJson(error);sendingErrorMessage(session, errorJson);
                        }
                    }
                    else // observer
                    {
                        ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);error.setErrorMessage("Observer should not make move.");
                        String errorJson = gson.toJson(error);sendingErrorMessage(session, errorJson);
                    }
                }
                else // not valid move
                {
                    ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("The move is not valid");
                    String errorJson = gson.toJson(error);
                    sendingErrorMessage(session, errorJson);}
            }
        }
        catch (DataAccessException | IOException | InvalidMoveException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void resign(String message, Session session) {
        try
        {
            GameData gameData = null;
            ChessGame chessGame = null;
            Gson gson = new Gson();
            Resign resign = gson.fromJson(message, Resign.class);
            int gameID = resign.getGameID();
            String authToken = resign.getAuthString();
            SQLGame sqlGame = new SQLGame();
            SQLUser sqlUser = new SQLUser();
            SQLAuth sqlAuth = new SQLAuth();
            String username = sqlAuth.getAuth(authToken);
            if (username == null)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Unauthorized.");
                String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            try
            {
                gameData = sqlGame.getGame(gameID);chessGame = gameData.game();
            }
            catch (DataAccessException e)
            {
                ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                error.setErrorMessage("Game is not found.");String errorJson = gson.toJson(error);
                sendingErrorMessage(session, errorJson);
            }
            if (username != null && gameData != null)
            {
                if (username.equals(gameData.whiteUsername()))
                {
                    if (chessGame.isResigned != true)
                    {
                        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.WHITE);
                        notification.setMessage(username + " resigns the game.");
                        String messageJson = gson.toJson(notification);
                        CONNECTION_MANAGER.broadcast(gameID, session, messageJson); // send to everyone else
                        Connection resignMaker = new Connection(authToken, session);
                        if (resignMaker.session.isOpen()) // and send to myself
                        {
                            resignMaker.send(messageJson);
                        }

                        chessGame.isResigned = true;
                        sqlGame.updateChessGame(chessGame, gameID);
                    }
                    else
                    {
                        ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                        error.setErrorMessage("You cannot resign after one player already resigned.");
                        String errorJson = gson.toJson(error);
                        sendingErrorMessage(session, errorJson);
                    }

                }
                else if (username.equals(gameData.blackUsername()))
                {
                    if (chessGame.isResigned != true)
                    {
                        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, username, ChessGame.TeamColor.BLACK);
                        notification.setMessage(username + " resigns the game.");
                        String messageJson = gson.toJson(notification);
                        CONNECTION_MANAGER.broadcast(gameID, null, messageJson);
                        Connection resignMaker = new Connection(authToken, session);
//                        if (resignMaker.session.isOpen())
//                        {
//                            resignMaker.send(messageJson);
//                        }

                        chessGame.isResigned = true;
                        sqlGame.updateChessGame(chessGame, gameID);
                    }
                    else
                    {
                        ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                        error.setErrorMessage("You cannot resign after one player already resigned.");
                        String errorJson = gson.toJson(error);
                        sendingErrorMessage(session, errorJson);
                    }
                }
                else // observer
                {
                    ErrorWebsocket error = new ErrorWebsocket(ServerMessage.ServerMessageType.ERROR);
                    error.setErrorMessage("Observer cannot resign.");
                    String errorJson = gson.toJson(error);
                    sendingErrorMessage(session, errorJson);
                }
            }
        }
        catch (DataAccessException | IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
