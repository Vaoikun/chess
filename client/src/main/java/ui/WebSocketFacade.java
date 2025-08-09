package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import com.google.gson.Gson;
import websocket.commands.websocketrequest.Connect;
import websocket.commands.websocketrequest.Leave;
import websocket.commands.websocketrequest.MakeMove;
import websocket.commands.websocketrequest.Resign;
import websocket.messages.ServerMessage;
import websocket.messages.websocketresponse.LoadGame;

import javax.management.Notification;
import javax.websocket.*;
import javax.xml.stream.events.StartDocument;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class WebSocketFacade extends Endpoint {
    Session session;
    public ChessGame.TeamColor teamColor;
    public ChessGame chessGame;

    public WebSocketFacade (String URL, ChessGame.TeamColor teamColor, ChessGame chessGame) {
        this.teamColor = teamColor;
        this.chessGame = chessGame;
        try {
            URL = URL.replace("http", "ws");
            URI webSocketURI = new URI(URL + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, webSocketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case ServerMessage.ServerMessageType.NOTIFICATION -> sendBackNotification(message);
                        case ServerMessage.ServerMessageType.ERROR -> sendBackError(message);
                        case ServerMessage.ServerMessageType.LOAD_GAME -> sendLoadGameBack(message);
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendBackNotification(String message) {
        Gson json = new Gson();
        Notification notification = json.fromJson(message, Notification.class);
        System.out.println(notification.getMessage());
    }

    public void sendBackError(String message) {
        Gson json = new Gson();
        Error error = json.fromJson(message, Error.class);
        System.out.println(error.getMessage());
    }

    public void sendLoadGameBack(String message) {
        Gson json = new Gson();
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        LoadGame loadGame = json.fromJson(message, LoadGame.class);
        ChessGame chessGame = loadGame.getGame();
        this.chessGame = chessGame;
        ChessBoard chessBoard = chessGame.getBoard();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            BoardUI.callWhiteTiles(out, chessBoard, null);
        } else if (teamColor == ChessGame.TeamColor.BLACK) {
            BoardUI.callBlackTiles(out, chessBoard, null);
        }
    }

    public void setChessGame(ChessGame chessGame) {
        this.chessGame = chessGame;
    }
    public ChessGame getChessGame(){
        return this.chessGame;
    }
    public void setTeamColor(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    public void connectPlayer (String authToken, int gameID) throws IOException {
        Gson json = new Gson();
        Connect connectPlayer = new Connect(authToken, gameID);
        String connectPlayerMessage = json.toJson(connectPlayer);
        this.session.getBasicRemote().sendText(connectPlayerMessage);
    }

    public void makeMove (String authToken, int gameID, ChessMove chessMove) throws IOException {
        Gson json = new Gson();
        MakeMove makeMove = new MakeMove(authToken, gameID, chessMove);
        String message = json.toJson(makeMove);
        this.session.getBasicRemote().sendText(message);
    }

    public void leave (String authToken, int gameID) throws IOException {
        Gson json = new Gson();
        Leave leave = new Leave(authToken, gameID);
        String message = json.toJson(leave);
        this.session.getBasicRemote().sendText(message);
    }

    public void resign (String authToken, int gameID) throws IOException {
        Gson json = new Gson();
        Resign resign = new Resign(authToken, gameID);
        String message = json.toJson(resign);
        this.session.getBasicRemote().sendText(message);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {

    }
}
