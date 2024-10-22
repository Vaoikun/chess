package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    private static final String CREATE_CALL =
            """ 
           CREATE TABLE IF NOT EXISTS (
                passwordCol varchar(255) NOT NULL,
                usernameCol varchar(255) NOT NULL,
                emailCol varchar(255) NOT NULL,
                PRIMARY KEY (usernameCol)
                )
           """;

    public SQLUserDAO() throws DataAccessException{
        createUserTable();
    }

    public static void createUserTable() throws DataAccessException {
        try(var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement(CREATE_CALL)) {
                prepStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String password = user.password();
        String hashedPswrd = HashedPassword.hashPassword(password);
        try (var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement("INSERT INTO Users(usernameCol, passwordCol, emailCol) VALUES (?, ?, ?);")) {
                prepStatement.setString(1, user.username());
                prepStatement.setString(2, hashedPswrd);
                prepStatement.setString(3, user.email());

                prepStatement.executeUpdate();
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try(var connct = DatabaseManager.getConnection()){
            try(var prepStatement = connct.prepareStatement("TRUNCATE TABLE Users;")){
                prepStatement.executeUpdate();
            }
        }catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData userData = null;
        if (username == null) {return null;}
        try (var connct = DatabaseManager.getConnection()){
            try(var prepStatement = connct.prepareStatement("SELECT passwordCol, usernameCol, emailCol FROM Users WHERE usernameCol = ?;")){
                prepStatement.setString(1, username);
                try(var userInfo = prepStatement.executeQuery()){
                    while (userInfo.next()){
                        String password = userInfo.getString("passwordCol");
                        String email = userInfo.getString("emailCol");
                        userData = new UserData(username, password, email);
                        break;
                    }
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
                return userData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
