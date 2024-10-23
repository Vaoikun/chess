package dataaccess;

import model.GameData;
import chess.ChessGame;

import java.util.ArrayList;

public interface GameDAO {
    public default int createGame(String gameName) throws DataAccessException {
        return 1;
    }

    public default GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public default ArrayList<GameData> listGames(String authToken) throws DataAccessException
    {
        return null;
    }

    public default void updateGame(String username, ChessGame.TeamColor playerColor, GameData targetGame) throws DataAccessException
    {
    }

    public default void clear() throws DataAccessException
    {
        return;
    }

    public default void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException
    {
        return;
    }



}
