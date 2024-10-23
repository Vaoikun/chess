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

    @Override
    public int createGame(String gameName) throws DataAccessException {
        Random random = new Random();
        int newGameID = random.nextInt(10000);
        GameData newGame = new GameData(newGameID, null, null, gameName, new ChessGame(ChessGame.TeamColor.WHITE, new ChessBoard()));
        GAME_DATA_MEMORY.add(newGame);
        return newGameID;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData gameData : GAME_DATA_MEMORY) {
            if (gameData.gameID() == gameID) {
                return gameData;
            }
        }
        return null;
    }

    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        return new ArrayList<>(GAME_DATA_MEMORY);
    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData requestedGame) throws DataAccessException {
        GameData updatedGame;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            updatedGame = new GameData(currentGame.gameID(), username, currentGame.blackUsername(), currentGame.gameName(), currentGame.game());
            requestedGame = updatedGame;
            GAME_DATA_MEMORY.add(requestedGame);
        }
        else if (playerColor == ChessGame.TeamColor.BLACK) {
            updatedGame = new GameData(currentGame.gameID(), username, currentGame.whiteUsername(), currentGame.gameName(), currentGame.game());
            requestedGame = updatedGame;
            GAME_DATA_MEMORY.add(requestedGame);
        }
    }

    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData game = getGame(gameID);
        currentGame = game;
        GAME_DATA_MEMORY.remove(game);
        updateGame(username, playerColor, game);
    }

    @Override
    public void clear() throws DataAccessException {
        GAME_DATA_MEMORY.clear();
    }
}

