package websocket.messages.websocketresponse;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class Notification extends ServerMessage {
    private String username;
    private String message;
    private ChessGame.TeamColor joinedTeamColor;

    public Notification(ServerMessageType type, String username, ChessGame.TeamColor joinedTeamColor) {
        super(type);
        this.username = username;
        this.joinedTeamColor = joinedTeamColor;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
