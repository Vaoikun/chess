package dataaccess;

public interface AuthDAO {

    default void clear() throws DataAccessException {return;}

    default String createAuth(String username) throws DataAccessException {return null;}

    default String getAuth(String username) throws DataAccessException {return null;}

    default String getUsername(String authToken) throws DataAccessException {return null;}

    default void deleteAuth(String authToken) throws DataAccessException {}

}
