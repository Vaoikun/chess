package websocket.messages.websocketresponse;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class LoadGame extends ServerMessage {
    private ChessGame chessGame;

    public LoadGame(ServerMessageType type, ChessGame chessGame) {
        super(type);
        this.chessGame = chessGame;
    }

    public ChessGame getGame() {
        return chessGame;
    }
}
