package dataaccess;

public interface AuthDAO {

    default void clear() throws DataAccessException {return;}
}
