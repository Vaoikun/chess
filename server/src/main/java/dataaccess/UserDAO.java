package dataaccess;

import model.UserData;

public interface UserDAO {

    default void clear() throws DataAccessException {return;}

    default void createUser(UserData userData) throws DataAccessException {return;}

    default UserData getUser(String username) throws DataAccessException {return null;}

}
