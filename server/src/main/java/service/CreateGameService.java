package service;

import httprequest.CreateGameRequest;
import httpresponse.CreateGameResponse;
import dataaccess.*;

public class CreateGameService
{
    private final SQLGame gameDB = new SQLGame();
    private final SQLAuth authDB = new SQLAuth();

    public CreateGameService() throws DataAccessException {
    }

    public CreateGameResponse createGame(CreateGameRequest createGameRequest, String authToken) throws DataAccessException, ClientException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        else
        {
            int gameID = gameDB.createGame(createGameRequest.gameName());
            return new CreateGameResponse(gameID);
        }
    }
}
