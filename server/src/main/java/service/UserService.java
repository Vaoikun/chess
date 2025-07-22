package service;

import dataaccess.*;
import server.ServerException;

public class UserService {

    private final GameMDAO gameDB = new GameMDAO();
    private final UserMDAO userDB = new UserMDAO();
    private final AuthMDAO authDB = new AuthMDAO();

    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        userDB.clear();
        authDB.clear();
    }
}
