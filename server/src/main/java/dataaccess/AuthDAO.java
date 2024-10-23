package dataaccess;

import java.sql.SQLException;

public interface AuthDAO {

    public  String createAuth(String username) throws DataAccessException, SQLException;

    public String getAuth(String authToken) throws DataAccessException, SQLException;

    public void deleteAuth(String authToken) throws DataAccessException, SQLException;

    public  void clear() throws DataAccessException;
}
