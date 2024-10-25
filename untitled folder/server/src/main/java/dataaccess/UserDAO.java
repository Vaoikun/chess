package dataaccess;

import model.UserData;

public interface UserDAO {

    default void createUser(UserData u) throws DataAccessException{
        return;
    }

    default void clear() throws DataAccessException
    {
        return;
    }

    default UserData getUser(String username) throws DataAccessException
    {
        return null;
    }

}
