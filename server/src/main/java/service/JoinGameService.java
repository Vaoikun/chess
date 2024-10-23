package service;

import chess.PawnMovesCalculator;
import httprequest.JoinGameRequest;
import model.GameData;
import chess.ChessGame;
import dataaccess.*;

public class JoinGameService
{
    SQLAuth authDB = new SQLAuth();
    SQLGame gameDB = new SQLGame();

    public JoinGameService() throws DataAccessException {
    }

    public void joinGame (JoinGameRequest joinGameRequest, String authToken) throws DataAccessException, ServerException, ClientException, AlreadyTakenException
    {
        String username = authDB.getAuth(authToken);
        if (username == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        if (joinGameRequest.playerColor() == null || joinGameRequest.gameID() == 0)
        {
            throw new ClientException("Error: bad request");
        }
        else
        {
            GameData foundGame = gameDB.getGame(joinGameRequest.gameID());
            if (foundGame != null)
            {
                if (joinGameRequest.playerColor() == ChessGame.TeamColor.WHITE && foundGame.whiteUsername() != null || joinGameRequest.playerColor() == ChessGame.TeamColor.BLACK && foundGame.blackUsername() != null)
                {
                    throw new AlreadyTakenException("Error: Already Taken");
                }
                else
                {
                    // just need to call joinGame Because joinGame will call updateGame in it.
                    gameDB.joinGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), username);
                }
            }
            else
            {
                throw new DataAccessException("The game is null in db.");
            }
        }
    }

}
