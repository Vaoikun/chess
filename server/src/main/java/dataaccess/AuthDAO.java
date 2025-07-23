package dataaccess;

public interface AuthDAO {

    default void clear() throws DataAccessException {return;}

    default String createAuth(String username) throws DataAccessException {return null;}
}
