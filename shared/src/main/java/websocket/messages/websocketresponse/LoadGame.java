package websocket.messages.websocketresponse;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class LoadGame extends ServerMessage {
    private ChessGame game;

    public LoadGame(ServerMessageType type, ChessGame chessGame) {
        super(type);
        this.game = chessGame;
    }

    public ChessGame getGame() {
        return game;
    }
}
