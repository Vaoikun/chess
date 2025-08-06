package websocket.commands.websocketrequest;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMove extends UserGameCommand {
    private final ChessMove chessMove;

    public MakeMove(String authToken, Integer gameID, ChessMove chessMove) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.chessMove = chessMove;
    }

    public ChessMove getChessMove() {
        return this.chessMove;
    }
}
