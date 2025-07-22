package dataaccess;

public interface UserDAO {

    default void clear() throws DataAccessException {return;}
}
