package service;

import dataaccess.*;
import httpresponse.LIstGameResponse;
import model.GameData;

import java.util.ArrayList;

public class ListGamesService
{
    private final SQLAuth authDB = new SQLAuth();

    private final SQLGame gameDB = new SQLGame();

    public ListGamesService() throws DataAccessException {
    }

    public LIstGameResponse listGame(String authToken) throws DataAccessException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        else
        {
            ArrayList<GameData> listGames =  gameDB.listGames(authToken);
            return new LIstGameResponse(listGames);
        }
    }

}
