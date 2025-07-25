package dataaccess;

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
        try (var connect =DatabaseManager.getConnection()){
            try (var createStatement = connect.prepareStatement(CREATE_STATEMENT)) {
                createStatement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }


}
