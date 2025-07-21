package httprequest;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor teamColor, int gameID) {
}
