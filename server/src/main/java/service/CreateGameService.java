package service;

import httprequest.CreateGameRequest;
import httpresult.CreateGameResult;
import dataaccess.*;

public class CreateGameService {
    private final SQLGameDAO gameDB = new SQLGameDAO();
    private final SQLAuthDAO authDB = new SQLAuthDAO();

    public CreateGameService() throws DataAccessException {}

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException, ClientException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }else{
            int gameID = gameDB.createGame(request.gameName());
            return new CreateGameResult(gameID);
        }
    }
}
