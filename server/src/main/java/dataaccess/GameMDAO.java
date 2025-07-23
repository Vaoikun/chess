package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class GameMDAO implements GameDAO{

    private static final HashSet<GameData> GAME_DATA_HASH_SET = new HashSet<>();
    private GameData currentGame;

    @Override
    public void clear() throws DataAccessException{
        GAME_DATA_HASH_SET.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        int newGameID = random.nextInt(10000);
        GameData newGame = new GameData(newGameID, null, null, gameName, new ChessGame(ChessGame.TeamColor.WHITE, new ChessBoard()));
        GAME_DATA_HASH_SET.add(newGame);
        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : GAME_DATA_HASH_SET) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor teamColor, GameData gameRequest)
            throws DataAccessException {
        if (teamColor == ChessGame.TeamColor.BLACK) {
            GameData updatedGame = new GameData(currentGame.gameID(), currentGame.whiteUsername(), username,
                    currentGame.gameName(), currentGame.game());
            gameRequest = updatedGame;
            GAME_DATA_HASH_SET.add(gameRequest);
        } else if (teamColor == ChessGame.TeamColor.WHITE) {
            GameData updatedGame = new GameData(currentGame.gameID(), username, currentGame.blackUsername(),
                    currentGame.gameName(), currentGame.game());
            gameRequest = updatedGame;
            GAME_DATA_HASH_SET.add(gameRequest);
        }
    }

    @Override
    public void joinGame(int gameID, ChessGame.TeamColor teamColor, String username)
            throws DataAccessException {
        GameData gameData = getGame(gameID);
        currentGame = gameData;
        GAME_DATA_HASH_SET.remove(gameData);
        updateGame(username, teamColor, gameData);
    }

    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        return new ArrayList<>(GAME_DATA_HASH_SET);
    }
}
