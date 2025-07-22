package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import server.ServerException;

public class UserService {

    private final GameDAO gameDB = new GameDAO();
    private final UserDAO userDB = new UserDAO();
    private final AuthDAO authDB = new AuthDAO();

    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        userDB.clear();
        authDB.clear();
    }
}
