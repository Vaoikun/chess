package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {

    default void clear() throws DataAccessException {return;}

    default void createUser(UserData userData) throws DataAccessException, SQLException {return;}

    default UserData getUser(String username) throws DataAccessException {return null;}

}
