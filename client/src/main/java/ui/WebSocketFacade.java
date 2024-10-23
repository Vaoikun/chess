package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.websocketrequests.ConnectPlayer;
import websocket.commands.websocketrequests.Leave;
import websocket.commands.websocketrequests.MakeMove;
import websocket.commands.websocketrequests.Resign;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.ErrorWebsocket;
import websocket.messages.websocketresponse.LoadGame;
import websocket.messages.websocketresponse.Notification;

import javax.websocket.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class WebSocketFacade extends Endpoint
{
    Session session;

    NotificationHandler notificationHandler;

    private static ChessGame.TeamColor color;

    public ChessGame chessGame;

    public GameData gameData;

    public ChessGame.TeamColor getColor() {
        return color;
    }

    public  void setColor(ChessGame.TeamColor color) {
        WebSocketFacade.color = color;
    }

    public WebSocketFacade(String url, ChessGame.TeamColor color, ChessGame chessGame)
    {
        this.chessGame = chessGame;
        this.color = color;


        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>()
            {
                @Override
                public void onMessage(String message)
                {
                    // make the message send from server to be back to ServerMessage
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType())
                    {
                        case ServerMessage.ServerMessageType.NOTIFICATION -> sendingNotificationBack(message);
                        case ServerMessage.ServerMessageType.ERROR -> sendingErrorBack(message);
                        case ServerMessage.ServerMessageType.LOAD_GAME -> sendingLoadGameBack(message);
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendingNotificationBack(String message)
    {
        Gson gson = new Gson();
        Notification notification = gson.fromJson(message, Notification.class);
        System.out.println(notification.getMessage());
    }

    public void sendingErrorBack(String message)
    {
        Gson gson = new Gson();
        ErrorWebsocket error = gson.fromJson(message, ErrorWebsocket.class);
        System.out.println(error.getErrorMessage());
    }

    public void sendingLoadGameBack(String message)
    {
        Gson gson = new Gson();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        LoadGame loadGame = gson.fromJson(message, LoadGame.class);
        ChessGame game = loadGame.getGame();
        this.chessGame = game;
        ChessBoard board = game.getBoard();
        if (color == ChessGame.TeamColor.WHITE)
        {
            BoardUI.callWhiteBoard(out, board, null); // draw the white board to console

        }
        else if (color == ChessGame.TeamColor.BLACK)
        {
            BoardUI.callBlackBoard(out, board, null); // draw the black board to console
        }
    }

    public void connectPlayer(String authToken, int gameID) throws IOException // we don need to care about color, back-end check for us
    {   Gson gson = new Gson();
        ConnectPlayer connectPlayer = new ConnectPlayer(authToken, gameID);
        String connectPlayerJson = gson.toJson(connectPlayer);
        this.session.getBasicRemote().sendText(connectPlayerJson);
    }

    public void makeMove(String authToken, int gameID, ChessMove chessMove) throws IOException {
        Gson gson = new Gson();
        MakeMove makeMove = new MakeMove(authToken, gameID, chessMove);
        String makeMoveJson = gson.toJson(makeMove);
        this.session.getBasicRemote().sendText(makeMoveJson);
    }

    public void leave(String authToken, int gameID) throws IOException {
        Gson gson = new Gson();
        Leave leave = new Leave(authToken, gameID);
        String leaveJson = gson.toJson(leave);
        this.session.getBasicRemote().sendText(leaveJson);
    }

    public void resign(String authToken, int gameID) throws IOException {
        Gson gson = new Gson();
        Resign resign = new Resign(authToken, gameID);
        String resignJson = gson.toJson(resign);
        this.session.getBasicRemote().sendText(resignJson);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
