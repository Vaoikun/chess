package dataaccess;

import chess.ChessGame;
import model.GameData;
import server.ServerException;

import java.util.ArrayList;

public interface GameDAO {

    default void clear() throws DataAccessException, ServerException{}

    default int createGame(String gameName) throws DataAccessException {return 1;}

    default GameData getGame(int gameID) throws DataAccessException {return null;}

    default void updateGame(String username, ChessGame.TeamColor teamColor,
                                   GameData gameRequest) throws DataAccessException {}

    default void joinGame(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {}

    default ArrayList<GameData> listGames(String authToken) throws DataAccessException {return null;}
}
