package service;

import dataaccess.*;
import httprequest.LoginRequest;
import httpresult.LoginResult;
import model.UserData;

import javax.xml.crypto.Data;

public class LoginService {
    private final MemoryUserDAO userDB = new MemoryUserDAO();
    private final MemoryAuthDAO authDB = new MemoryAuthDAO();

    public LoginService() throws DataAccessException {}

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
