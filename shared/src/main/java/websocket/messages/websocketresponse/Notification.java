package websocket.messages.websocketresponse;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class Notification extends ServerMessage {
    private String username;
    private String message;
    private ChessGame.TeamColor teamColor;

    public Notification(ServerMessage.ServerMessageType type,
                        String username, ChessGame.TeamColor teamColor) {
        super(type);
        this.username = username;
        this.teamColor = teamColor;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
