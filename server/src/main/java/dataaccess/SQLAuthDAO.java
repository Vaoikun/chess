package dataaccess;

import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() throws DataAccessException {
        createAuthTable();
    }

    private static final String CREATE_STATEMENT =

            """
            CREATE TABLE IF NOT EXISTS Auths
            (
                authTokenCol varchar(255) NOT NULL,
                userNameCol varchar(255)  NOT NULL,
                 PRIMARY KEY (authTokenCol)
            )
            """
            ;

    public static void createAuthTable() throws DataAccessException {
        try (var connect = DatabaseManager.getConnection()){
            try (var prepStatement = connect.prepareStatement(CREATE_STATEMENT)){
                prepStatement.executeUpdate();
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        try (var connect = DatabaseManager.getConnection()) {
            if (username == null) {
                throw new DataAccessException("Username is null");
            }
            try (var prepStatement = connect.prepareStatement("INSERT INTO Auths(userNameCol, authTokenCol) VALUES(?, ?);")) {

                String authTokenCreated = UUID.randomUUID().toString(); // get a random authToken
                prepStatement.setString(1, username);
                prepStatement.setString(2, authTokenCreated);
                prepStatement.executeUpdate();
                return authTokenCreated;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        String username = null;
        try(var connect = DatabaseManager.getConnection()) {
            try (var prepStatement = connect.prepareStatement("SELECT authTokenCol, userNameCol FROM Auths WHERE authTokenCol = ?;")) {
                prepStatement.setString(1, authToken);
                try (var rs = prepStatement.executeQuery()) {
                    while (rs.next()) {
                        username = rs.getString("userNameCol");
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return username;
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException, SQLException {
        String username = null;
        try(var connect = DatabaseManager.getConnection()) {
            try (var prepStatement = connect.prepareStatement("SELECT authTokenCol, userNameCol FROM Auths WHERE authTokenCol = ?;")) {
                prepStatement.setString(1, authToken);
                try (var rs = prepStatement.executeQuery()) {
                    while (rs.next()) {
                        username = rs.getString("userNameCol");
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return username;
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {
        if (username == null) {
            throw new DataAccessException("username is null");
        }try (var connect = DatabaseManager.getConnection()) {
            // delete the row that matches the authToken
            try (var prepStatement = connect.prepareStatement("DELETE FROM Auths WHERE userNameCol = ?;")) {
                prepStatement.setString(1, username);
                prepStatement.executeUpdate();
            }
        }
        catch (SQLException e)
        {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connect = DatabaseManager.getConnection()) {
            try (var prepStatement = connect.prepareStatement("TRUNCATE TABLE Auths;")) {
                prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
