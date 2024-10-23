package dataaccess;

import model.GameData;
import chess.ChessBoard;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MemoryGameDAO implements GameDAO{

    private static final HashSet<GameData> GAME_DATA_MEMORY = new HashSet<>();
    /**
     * @param gameName
     * @return
     * @throws DataAccessException
     */

    private GameData usedGame;
    @Override
    public int createGame(String gameName) throws DataAccessException
    {
        Random random = new Random();
        int randomInt = random.nextInt(10000); // get the random number between 0 - 10000
        GameData newGame = new GameData(randomInt, null, null, gameName, new ChessGame(new ChessBoard(), ChessGame.TeamColor.WHITE));
        GAME_DATA_MEMORY.add(newGame);
        return randomInt;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData singleGame : GAME_DATA_MEMORY)
        {
            if (singleGame.gameID() == gameID)
            {
                return singleGame;
            }
        }
        return null;
    }

    /**
     * @param authToken
     * @return
     * @throws DataAccessException
     */
    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        return new ArrayList<>(GAME_DATA_MEMORY);
    }


    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData targetGame) throws DataAccessException {

        GameData temGame;
        if (playerColor.equals(ChessGame.TeamColor.WHITE))
        {
            temGame = new GameData(usedGame.gameID(), username, usedGame.blackUsername(), usedGame.gameName(), usedGame.game()); // create a temporary GameData. because we cannot change GameData's property
            targetGame = temGame;
            GAME_DATA_MEMORY.add(targetGame);
        }
        else if(playerColor.equals(ChessGame.TeamColor.BLACK))
        {
           temGame = new GameData(usedGame.gameID(), usedGame.whiteUsername(), username, usedGame.gameName(), usedGame.game());
           targetGame = temGame;
           GAME_DATA_MEMORY.add(targetGame); // then add a new updated one
        }

    }

    /**
     * @param gameID
     * @param playerColor
     * @param username
     * @throws DataAccessException
     */
    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        GameData foundGame = getGame(gameID);
        usedGame = foundGame;
        GAME_DATA_MEMORY.remove(foundGame); // we removed the old one first.
        updateGame(username, playerColor, foundGame);

    }

    /**
     * @throws DataAccessException
     */
    @Override
    public void clear() throws DataAccessException {
        GAME_DATA_MEMORY.clear();
    }


}
