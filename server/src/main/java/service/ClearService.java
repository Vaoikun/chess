package service;

import dataaccess.*;

public class ClearService
{
    private final SQLGame gameDB = new SQLGame();
    private final SQLAuth authDB = new SQLAuth();

    private final SQLUser userDB =  new SQLUser();

    public ClearService() throws DataAccessException {
    }


    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        authDB.clear();
        userDB.clear();
    }
}
