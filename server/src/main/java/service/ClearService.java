package service;

import dataaccess.*;

public class ClearService {
    private final MemoryGameDAO gameDB = new MemoryGameDAO();
    private final MemoryAuthDAO authDB = new MemoryAuthDAO();
    private final MemoryUserDAO userDB =  new MemoryUserDAO();

    public ClearService() throws DataAccessException {}

    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        authDB.clear();
        userDB.clear();
    }
}
