package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import org.junit.jupiter.api.Order;

import javax.xml.crypto.Data;
import java.sql.SQLException;
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

}
