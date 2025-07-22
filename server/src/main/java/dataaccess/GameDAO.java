package dataaccess;

import server.ServerException;

public interface GameDAO {

    public default void clear() throws DataAccessException, ServerException{return;}
}
