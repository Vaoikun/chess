package websocket.commands.websocketrequests;

import chess.ChessMove;
import websocket.commands.UserGameCommand;

public class MakeMove extends UserGameCommand {

    private int gameID;

    private ChessMove move;

    public MakeMove(String authToken, int gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public int getGameID() {
        return gameID;
    }


    public ChessMove getChessMove() {
        return move;
    }

}
