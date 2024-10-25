package service;

import httprequest.JoinGameRequest;
import model.GameData;
import chess.ChessGame;
import dataaccess.*;

public class JoinGameService {
    MemoryAuthDAO authDB = new MemoryAuthDAO();
    MemoryGameDAO gameDB = new MemoryGameDAO();

    public JoinGameService() throws DataAccessException {}

    public void joinGame (JoinGameRequest request, String authToken) throws DataAccessException, ServerException, ClientException, AlreadyTakenException {
        String username = authDB.getUsername(authToken);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (request.playerColor() == null || request.gameID() == 0) {
            throw new ClientException("Error: bad request");
        }else{
            GameData availableGame = gameDB.getGame(request.gameID());
            if (availableGame != null) {
                if (request.playerColor() == ChessGame.TeamColor.WHITE && availableGame.whiteUsername() != null || request.playerColor() == ChessGame.TeamColor.BLACK && availableGame.blackUsername() != null) {
                    throw new AlreadyTakenException("Error: Already Taken");
                }else{
                    // joinGame will call updateGame in it.
                    gameDB.joinGame(request.gameID(), request.playerColor(), username);
                }
            }else{
                throw new DataAccessException("The game is null.");
            }
        }
    }
}
