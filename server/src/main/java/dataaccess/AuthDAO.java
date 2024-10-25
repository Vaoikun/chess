package dataaccess;

import java.sql.SQLException;

/**
 * Interface side
 */
public interface AuthDAO {
    public String createAuth(String username) throws DataAccessException, SQLException;

    public String getAuth(String authToken) throws DataAccessException, SQLException;

    public String getUsername(String authToken) throws DataAccessException, SQLException;

    public void deleteAuth(String authToken) throws DataAccessException, SQLException;

    public void clear() throws DataAccessException;
}
