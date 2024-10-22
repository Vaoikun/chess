package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class SQLGameDAO implements GameDAO {
    private static final String CREATE_CALL =
            """
            CREATE TABLE IF NOT EXISTS Games
            (
                gameIDCol INT NOT NULL,
                whiteUserNameCol varchar(255),
                blackUserNameCol varchar(255),
                gameNameCol varchar(255) NOT NULL,
                ChessGameCol TEXT NOT NULL,
                 PRIMARY KEY (gameIDCol)
            )
            """;

    public SQLGameDAO() throws DataAccessException{
        createGameTable();
    }

    public static void createGameTable() throws DataAccessException {
        try(var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement(CREATE_CALL)) {
                prepStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if(gameName == null){throw new DataAccessException("Game name is null");}
        Gson gson = new Gson();
        try(var connct = DatabaseManager.getConnection()){
            try (var prepStatement = connct.prepareStatement("INSERT INTO Games(gameIDCol, blackuserNameCol, gameNameCol, ChessGameCol) VALUES (?, ?, ?, ?, ?);")){
                ChessGame newGame = new ChessGame();
                String jsonGame = gson.toJson(newGame);
                Random rand = new Random();
                int randomInt = rand.nextInt(10000);
                prepStatement.setInt(1, randomInt);
                prepStatement.setString(2, null);
                prepStatement.setString(3, null);
                prepStatement.setString(4, gameName);
                prepStatement.setString(5, jsonGame);
                prepStatement.executeUpdate();
                return randomInt;
            }catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String whiteUserName, blackUserName, gameName, chessGame;
        Gson gson = new Gson();
        GameData gameData;
        try(var connct = DatabaseManager.getConnection()){
            try(var prepStatement = connct.prepareStatement("SELECT gameIDCol, whiteUserNameCol, blackUserNameCol, gameNameCol, ChessGameCol FROM Games WHERE gameIDCol = ?;")){
                prepStatement.setInt(1, gameID);
                try(var gameInfo = prepStatement.executeQuery()){
                    if (gameInfo.next()){
                        whiteUserName = gameInfo.getString("whiteUserNameCol");
                        blackUserName = gameInfo.getString("blackUserNameCol");
                        gameName = gameInfo.getString("gameNameCol");
                        chessGame = gameInfo.getString("ChessGameCol");
                        ChessGame game = gson.fromJson(chessGame, ChessGame.class);
                        gameData = new GameData(gameID, whiteUserName, blackUserName, gameName, game);
                        return gameData;
                    }else{
                        throw new DataAccessException("Game not found");
                    }
                }catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        if (authToken == null) {throw new DataAccessException("AuthToken is null");}
        String whiteUserName, blackUserName, gameName, chessGame;
        int gameID;
        Gson gson = new Gson();
        ArrayList<GameData> gameslist = new ArrayList<>();
        try (var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement("SELECT * FROM Games;")) {
                try (var gameInfo = prepStatement.executeQuery()) {
                    while (gameInfo.next()){ // I think not if, because we need all gameData in db.{
                        gameID = gameInfo.getInt("gameIDCol");
                    whiteUserName = gameInfo.getString("whiteUserNameCol");
                    blackUserName = gameInfo.getString("blackUserNameCol");
                    gameName = gameInfo.getString("gameNameCol");
                    chessGame = gameInfo.getString("ChessGameCol");
                    ChessGame getGame = gson.fromJson(chessGame, chess.ChessGame.class);
                    gameslist.add(new GameData(gameID, whiteUserName, blackUserName, gameName, getGame));
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameslist;
    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData requestedGame) throws DataAccessException {
        int gameID = requestedGame.gameID();
        try (var connct = DatabaseManager.getConnection()) {
            if (playerColor == ChessGame.TeamColor.WHITE) {
                try (var prepStatement = connct.prepareStatement("UPDATE Games SET whiteUserNameCol = ? WHERE gameIDCol = ?;")) {
                    prepStatement.setString(1, username);
                    prepStatement.setInt(2, gameID);
                    prepStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } else {
                try (var preparedStatement = connct.prepareStatement("UPDATE Games SET blackUserNameCol = ? WHERE gameIDCol = ?;")) {
                    preparedStatement.setString(1, username);
                    preparedStatement.setInt(2, gameID);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement("TRUNCATE TABLE Games;")) {
                prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        if(username == null){throw new DataAccessException("Username is null");}
        GameData gameData = getGame(gameID);
        updateGame(username, playerColor, gameData);
    }
}
