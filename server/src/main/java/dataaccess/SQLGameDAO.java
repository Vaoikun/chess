package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import model.GameData;
import org.junit.jupiter.api.Order;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class SQLGameDAO implements GameDAO {

    private static final String CREATE_STATEMENT =
            """
                    CREATE TABLE IF NOT EXISTS Games(
                    gameIDCol INT NOT NULL,
                    whiteUsernameCol varchar(255),
                    blackUsernameCol varchar(255),
                    gameNameCol varchar(255) NOT NULL,
                    gameCol TEXT NOT NULL,
                    PRIMARY KEY (gameIDCol));""";


    public static void createGamesTable() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var createStatement = connection.prepareStatement(CREATE_STATEMENT)) {
                createStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public SQLGameDAO() throws DataAccessException {
        createGamesTable();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException("null gameName.");
        }
        Gson gson = new Gson();
        try (var connection = DatabaseManager.getConnection()) {
            try (var createStatement = connection.prepareStatement(
                    "INSERT INTO Games(" +
                            "gameIDCol, whiteUsernameCol, blackUsernameCol, gameNameCol, gameCol)" +
                            "VALUES (?, ?, ?, ?, ?));"
            )) {
                ChessGame newGame = new ChessGame();
                String json = gson.toJson(newGame);
                Random randomizer = new Random();
                int randomInt = randomizer.nextInt(10000);
                createStatement.setInt(1, randomInt);
                createStatement.setString(2, null);
                createStatement.setString(3, null);
                createStatement.setString(4, gameName);
                createStatement.setString(5, json);
                createStatement.executeUpdate();
                return randomInt;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var truncateStatement = connection.prepareStatement(
                    "TRUNCATE TABLE Games;"
            )) {
                truncateStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String whiteUsername, blackUsername, gameName, game;
        Gson json = new Gson();
        GameData gameData;
        try (var connection = DatabaseManager.getConnection()) {
            try (var selectStatement = connection.prepareStatement(
                    "SELECT gameIDCol, whiteUsernameCol, blackUsernameCol, gameNameCol, gameCol " +
                            "FROM Games WHERE gameIDCol = ?;"
            )) {
                selectStatement.setInt(1, gameID);
                try (var returnedData = selectStatement.executeQuery()) {
                    if (returnedData.next()) {
                        whiteUsername = returnedData.getString("whiteUsernameCol");
                        blackUsername = returnedData.getString("blackUsernameCol");
                        gameName = returnedData.getString("gameNameCol");
                        game = returnedData.getString("gameCol");
                        ChessGame chessGame = json.fromJson(game, chess.ChessGame.class);
                        gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                        return gameData;
                    } else {
                        throw new DataAccessException("game not found.");
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Error: null authToken.");
        }
        String whiteUsername, blackUsername, gameName, game;
        int gameID;
        Gson json = new Gson();
        ArrayList<GameData> gameList = new ArrayList<>();
        try (var connection = DatabaseManager.getConnection()) {
            try (var selectStatement = connection.prepareStatement(
                    "SELECT * FROM Games;"
            )) {
                try (var returnedData = selectStatement.executeQuery()) {
                    while (returnedData.next()) {
                        gameID = returnedData.getInt("gameIDCol");
                        whiteUsername = returnedData.getString("whiteUsernameCol");
                        blackUsername = returnedData.getString("blackUsernameCol");
                        gameName = returnedData.getString("gameNameCol");
                        game = returnedData.getString("gameCol");
                        ChessGame chessGame = json.fromJson(game, chess.ChessGame.class);
                        gameList.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameList;
    }

    @Override
    public void updateGame(String username, ChessGame.TeamColor playerColor, GameData gameRequest)
        throws DataAccessException {
        int gameID = gameRequest.gameID();
        String UPDATE_STATEMENT;
        if (playerColor == ChessGame.TeamColor.WHITE) {
            UPDATE_STATEMENT = "UPDATE Games SET whiteUsernameCol = ? WHERE gameIDCol = ?;";
        } else {
            UPDATE_STATEMENT = "UPDATE Games SET blackUsernameCol = ? WHERE gameIDCol = ?;";
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var updateStatement = connection.prepareStatement(UPDATE_STATEMENT)) {
                updateStatement.setString(1, username);
                updateStatement.setInt(2, gameID);
                updateStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("Error: null username.");
        }
        GameData game = getGame(gameID);
        updateGame(username, playerColor, game);
    }

    public void updateGameBoard (ChessGame game, int gameID) throws DataAccessException {
        Gson gson = new Gson();
        String json = gson.toJson(game);
        try (var connection = DatabaseManager.getConnection()) {
            try (var updateStatement = connection.prepareStatement(
                    "UPDATE Games SET gameCol = ?, WHERE gameIDCol = ?"
            )) {
                updateStatement.setString(1, json);
                updateStatement.setInt(2, gameID);
                updateStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
