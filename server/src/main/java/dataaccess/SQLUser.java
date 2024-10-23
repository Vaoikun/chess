package dataaccess;

import model.UserData;

import java.sql.SQLException;

public class SQLUser implements UserDAO
{
    /**
     * @param u
     * @throws DataAccessException
     */
    private static final String CREATE_STATEMENT =
                    // email is varchar or text?
                    """
                    CREATE TABLE IF NOT EXISTS Users (
                        passwordCol varchar(255) NOT NULL,
                        usernameCol varchar(255)  NOT NULL,
                        emailCol varchar(255) NOT NULL,
                        PRIMARY KEY (usernameCol)
                    )
                    """
            ;

    public SQLUser() throws DataAccessException {
        createUserTable();
    }

    public static void createUserTable() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement(CREATE_STATEMENT))
            {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e)
            {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData u) throws DataAccessException
    {
        String password = u.password();
        String hashed = HashedPassword.hashPassword(password);
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("INSERT INTO Users(usernameCol, passwordCol, emailCol) VALUES (?, ?, ?);"))
            {
                preparedStatement.setString(1, u.username());
                preparedStatement.setString(2, hashed);
                preparedStatement.setString(3, u.email());

                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @throws DataAccessException
     */
    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("TRUNCATE TABLE Users;"))
            {
                preparedStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param username
     * @return
     * @throws DataAccessException
     */
    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData getUserData = null;
        if (username == null)
        {
            return null;
        }
        try (var conn = DatabaseManager.getConnection())
        {
            try (var preparedStatement = conn.prepareStatement("SELECT passwordCol, usernameCol, emailCol FROM Users WHERE usernameCol = ?;"))
            {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery())
                {
                    while (rs.next())
                    {
                        String password = rs.getString("passwordCol");
                        String email = rs.getString("emailCol");
                        getUserData = new UserData(username, password, email);
                        break;
                    }

                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
                return getUserData;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
