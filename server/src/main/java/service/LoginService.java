package service;

import dataaccess.*;
import httprequest.LoginRequest;
import httpresult.LoginResult;
import model.UserData;

import javax.xml.crypto.Data;

public class LoginService {
    private final SQLUserDAO userDB = new SQLUserDAO();
    private final SQLAuthDAO authDB = new SQLAuthDAO();

    public LoginService() throws DataAccessException {}

    /**
     * @param loginRequest;
     * @return new LoginResult;
     * @throws DataAccessException;
     * @throws ServerException;
     */
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException, ServerException {
        UserData userData = userDB.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (loginRequest.password() == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!HashedPassword.checkPassword(loginRequest.password(), userData.username())) {
            throw new DataAccessException("Error: unauthorized");
        }else{
            String authToken = authDB.createAuth(userData.username());
            return new LoginResult(userData.username(), authToken);
        }
    }
}
