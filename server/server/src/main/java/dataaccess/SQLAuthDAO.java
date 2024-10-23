package dataaccess;

//import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        createAuthTable();
    }

    private static final String CREATE_CALL =

            // the varChar is 255 or 256? They are null or not null.
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
        try (var connct = DatabaseManager.getConnection()){
            try(var prepStatement = connct.prepareStatement(CREATE_CALL)){
                prepStatement.executeUpdate();
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        try (var connct = DatabaseManager.getConnection()){
            if (username == null) {throw new DataAccessException("Username is null");}
            try (var prepStatement = connct.prepareStatement("INSERT INTO Auths(userNameCol, authTokenCol) VALUES(?, ?);")){
                String authToken = UUID.randomUUID().toString(); //random authToken
                prepStatement.setString(1, username);
                prepStatement.setString(2, authToken);
                prepStatement.executeUpdate();
                return authToken;
            }
        }catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getAuth(String authToken) throws DataAccessException {
        String username = null;
        try (var connct = DatabaseManager.getConnection()){
            try (var prepStatement = connct.prepareStatement("SELECT authTokenCol, userNameCol FROM Auths WHERE authTokenCol = ?;")){
                prepStatement.setString(1, authToken);
                try (var authInfo = prepStatement.executeQuery()){
                    while (authInfo.next()){
                        username = authInfo.getString("userNameCol");
                    }
                }
            }
        }catch(SQLException e){
            throw new DataAccessException(e.getMessage());
        }
        return username;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException{
        if (authToken == null) {
            throw new DataAccessException("AuthToken is null");}
        try (var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement("DELETE FROM Auths WHERE authTokenCol = ?;")){ // delete matching row
                prepStatement.setString(1, authToken);
                prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connct = DatabaseManager.getConnection()) {
            try (var prepStatement = connct.prepareStatement("TRUNCATE TABLE Auths;")) {
                prepStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
