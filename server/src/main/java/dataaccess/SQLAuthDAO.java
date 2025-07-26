package dataaccess;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    private static final String CREATE_STATEMENT =
            """
                    CREATE TABLE IF NOT EXIST Auths (
                    authTokenCol varchar(255) NOT NULL,
                    usernameCol varchar(255) NOT NULL,
                    PRIMARY KEY (authTokenCol));""";

    public SQLAuthDAO() throws DataAccessException {
        createAuthTable();
    }

    public static void createAuthTable () throws DataAccessException {
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

    @Override
    public String createAuth (String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var createStatement = connection.prepareStatement(
                    "INSERT INTO Auths(usernameCol, authTokenCol) VALUES (?, ?);"
            )) {
                String authToken = UUID.randomUUID().toString();
                createStatement.setString(1, username);
                createStatement.setString(2, authToken);
                createStatement.executeUpdate();
                return authToken;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getAuth (String username) throws DataAccessException {
        String authToken = null;
        try(var connection = DatabaseManager.getConnection()) {
            try (var selectStatement = connection.prepareStatement(
                    "SELECT authTokenCol, usernameCol FROM Auths WHERE usenameCol = ?;"
            )) {
                selectStatement.setString(1, username);
                try (var returnedData = selectStatement.executeQuery()) {
                    while (returnedData.next()) {
                        authToken = returnedData.getString("authTokenCol");
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
        return authToken;
    }

    @Override
    public String getUsername (String authToken) throws DataAccessException {
        String username = null;
        try(var connection = DatabaseManager.getConnection()) {
            try (var selectStatement = connection.prepareStatement(
                    "SELECT authTokenCol, usernameCol FROM Auths WHERE authTokenCol = ?;"
            )) {
                selectStatement.setString(1, username);
                try (var returnedData = selectStatement.executeQuery()) {
                    while (returnedData.next()) {
                        username = returnedData.getString("usernameCol");
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
        return username;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("null authToken.");
        }
        try (var connection = DatabaseManager.getConnection()) {
            try (var deleteStatement = connection.prepareStatement(
                    "DELETE FROM Auths WHERE authTokenCol = ?;"
            )) {
                deleteStatement.setString(1, authToken);
                deleteStatement.executeUpdate();
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
                    "TRUNCATE TABLE Auths;"
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
