package httprequest;

import chess.ChessGame;
import com.sun.net.httpserver.Request;

public record JoinGameRequest(ChessGame.TeamColor playerColor, int gameID) {
}
