package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    private static String CREATE_STATEMENT =
            """
                    CREATE TABLE IF NOT EXISTS Users (
                    passwordCol varchar(255) NOT NULL,
                    usernameCol varchar(255) NOT NULL,
                    emailCol varchar(255) NOT NULL,
                    PRIMARY KEY (usernameCol))
                    """;

    public SQLUserDAO() throws DataAccessException {
        createUserTable();
    }

    public static void createUserTable() throws DataAccessException {
        try (var connection =DatabaseManager.getConnection()){
            try (var createStatement = connection.prepareStatement(CREATE_STATEMENT)) {
                createStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var truncateStatement = connection.prepareStatement(
                    "TRUNCATE TABLE Users;"
            )) {
                truncateStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException, SQLException {
        String password = userData.password();
        String hashedPassword = HashPassword.hashPassword(password);
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(
                    "INSERT INTO Users(usernameCol, passwordCol, emailCol) VALUES (?, ?, ?);"
            )) {
                preparedStatement.setString(1, userData.username());
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, userData.email());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (username == null) {
            return null;
        }
        UserData userData = null;
        try (var connection = DatabaseManager.getConnection()) {
            try (var selectStatement = connection.prepareStatement(
                    "SELECT passwordCol, usernameCol, emailCol FROM Users WHERE usernameCol = ?;"
            )) {
                selectStatement.setString(1, username);
                try (var returnedData = selectStatement.executeQuery()) {
                    if (returnedData.next()) {
                        String password = returnedData.getString("passwordCol");
                        String email = returnedData.getString("emailCol");
                        userData = new UserData(username, password, email);
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
                return userData;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
