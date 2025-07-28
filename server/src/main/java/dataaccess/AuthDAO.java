package dataaccess;

import server.ServerException;

public interface AuthDAO {

    default void clear() throws DataAccessException, ServerException {return;}

    default String createAuth(String username) throws DataAccessException, ServerException {return null;}

    default String getAuth(String username) throws DataAccessException, ServerException {return null;}

    default String getUsername(String authToken) throws DataAccessException, ServerException {return null;}

    default void deleteAuth(String authToken) throws DataAccessException, ServerException {}

}
