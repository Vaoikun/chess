package dataaccess;

import model.GameData;
import chess.ChessBoard;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Random;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    private static final HashSet<GameData> GAME_DATA_MEMORY = new HashSet<>();

    private GameData currentGame;

    /**
     * @param gameName;
     * @return new game ID
     * @throws DataAccessException;
     */
    @Override
    public int createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        int newGameID = random.nextInt(10000);
        GameData newGame = new GameData(newGameID, gameName, null, null, new ChessGame(ChessGame.TeamColor.WHITE, new ChessBoard()));
        GAME_DATA_MEMORY.add(newGame);
        return newGameID;
    }

    /**
     * @param gameID;
     * @return gameData
     * @throws DataAccessException;
     */
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : GAME_DATA_MEMORY) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    /**
     * @param authToken;
     * @return ArrayList of GAME_DATA_MEMORY
     * @throws DataAccessException;
     */
    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        return new ArrayList<>(GAME_DATA_MEMORY);
    }

    /**
     * @param username;
     * @param playerColor;
     * @param requestedGame;
     * @throws DataAccessException;
     */
    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData requestedGame) throws DataAccessException {
        GameData updatedGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(currentGame.gameID(), currentGame.gameName(), username, currentGame.blackUsername(), currentGame.game());
            requestedGame = updatedGame;
            GAME_DATA_MEMORY.add(requestedGame);
        }
        else if (playerColor == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(currentGame.gameID(), currentGame.gameName(), currentGame.whiteUsername(), username, currentGame.game());
            requestedGame = updatedGame;
            GAME_DATA_MEMORY.add(requestedGame);
        }
    }

    /**
     * @param gameID;
     * @param playerColor;
     * @param username;
     * @throws DataAccessException;
     */
    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        currentGame = game;
        GAME_DATA_MEMORY.remove(game);
        updateGame(username, playerColor, game);
    }

    /**
     * @throws DataAccessException;
     */
    @Override
    public void clear() throws DataAccessException {
        GAME_DATA_MEMORY.clear();
    }
}

