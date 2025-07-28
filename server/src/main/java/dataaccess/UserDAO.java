package dataaccess;

import model.UserData;
import server.ServerException;

import java.sql.SQLException;

public interface UserDAO {

    default void clear() throws DataAccessException, ServerException {return;}

    default void createUser(UserData userData) throws DataAccessException, SQLException, ServerException {return;}

    default UserData getUser(String username) throws DataAccessException, ServerException {return null;}

}
