package service;

import dataaccess.*;

public class ClearService {
    private final SQLGameDAO gameDB = new SQLGameDAO();
    private final SQLAuthDAO authDB = new SQLAuthDAO();
    private final SQLUserDAO userDB =  new SQLUserDAO();

    public ClearService() throws DataAccessException {}

    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        authDB.clear();
        userDB.clear();
    }
}
