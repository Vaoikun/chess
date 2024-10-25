package service;

import dataaccess.*;
import httpresult.ListGameResult;
import model.GameData;
import java.util.ArrayList;

public class ListGamesService {
    private final MemoryAuthDAO authDB = new MemoryAuthDAO();
    private final MemoryGameDAO gameDB = new MemoryGameDAO();

    public ListGamesService() throws DataAccessException {}

    /**
     * @param authToken;
     * @return new ListGameResult;
     * @throws DataAccessException;
     * @throws ServerException;
     */
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
