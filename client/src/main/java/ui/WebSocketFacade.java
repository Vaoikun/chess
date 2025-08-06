package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import javax.websocket.*;
import java.net.URI;

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
                    }
                }
            });
        }
    }

    public void sendBackNotification(String message) {
        Gson json = new Gson();
        Notification notification = json.fromJson(message, Notification.class);
        System.out.println(notification.getMessage());
    }

    public void void sendBackError(String message) {
        Gson json = new Gson();
        Error error = json.fromJson(message, Error.class);
        System.out.println(error.getMessage());
    }
}
