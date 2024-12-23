package service;

import dataaccess.*;
import httpresult.ListGameResult;
import model.GameData;
import java.util.ArrayList;

public class ListGamesService {
    private final SQLAuthDAO authDB = new SQLAuthDAO();
    private final SQLGameDAO gameDB = new SQLGameDAO();

    public ListGamesService() throws DataAccessException {}

    public ListGameResult listGame(String authToken) throws DataAccessException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }else{
            ArrayList<GameData> gamesList = gameDB.listGames(authToken);
            return new ListGameResult(gamesList);
        }
    }
}
