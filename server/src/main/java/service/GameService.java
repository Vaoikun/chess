package service;

import chess.ChessGame;
import dataaccess.*;
import httprequest.CreateGameRequest;
import httprequest.JoinGameRequest;
import httpresponse.CreateGameResponse;
import httpresponse.ListGameResponse;
import model.GameData;
import server.ServerException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameService {

    private final GameMDAO gameDB = new GameMDAO();
    private final UserMDAO userDB = new UserMDAO();
    private final AuthMDAO authDB = new AuthMDAO();

    public GameService() throws DataAccessException {}

    public CreateGameResponse createGame(CreateGameRequest request, String authToken)
            throws DataAccessException, ServerException, ClientException {
        String username = authDB.getUsername(authToken);
        if (request.gameName() == null) {
            throw new ClientException("Error: bad request.");
        }
        if (username == null){
            throw new DataAccessException("Error: unauthorized.");
        } else {
            int gameID = gameDB.createGame(request.gameName());
            return new CreateGameResponse(gameID);
        }
    }

    public void joinGame (JoinGameRequest request, String authToken)
        throws DataAccessException, ServerException, ClientException, FullGameException {
        String username = authDB.getUsername(authToken);
        if (username == null){
            throw new DataAccessException("Error: unauthorized.");
        }
        if (request.playerColor() == null || request.gameID() == 0){
            throw new ClientException("Error: bad request.");
        } else {
            GameData selectedGame = gameDB.getGame(request.gameID());
            if (selectedGame != null) {
                if ((request.playerColor() == ChessGame.TeamColor.WHITE && selectedGame.whiteUsername() != null)
                || (request.playerColor() == ChessGame.TeamColor.BLACK && selectedGame.blackUsername() != null)) {
                    throw new FullGameException("Error: spot taken.");
                } else {
                    gameDB.joinGame(request.gameID(), request.playerColor(), username);
                }
            } else {
                throw new DataAccessException("Error: null game.");
            }
        }
    }

    public ListGameResponse listGames(String authToken)
            throws DataAccessException, ServerException, ClientException {
        String username = authDB.getUsername(authToken);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized.");
        } else {
            ArrayList<GameData> gameList = gameDB.listGames(authToken);
            return new ListGameResponse(gameList);
        }
    }
}

