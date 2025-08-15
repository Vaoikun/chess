package websocket.commands.websocketrequest;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMove extends UserGameCommand {
    private ChessMove move;

    public MakeMove(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getChessMove() {
        return move;
    }
}
