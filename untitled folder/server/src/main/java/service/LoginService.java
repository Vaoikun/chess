package service;

import dataaccess.*;
import httprequest.LoginRequest;
import httpresponse.LoginResponse;
import model.UserData;

public class LoginService
{
    private final SQLUser userDB = new SQLUser();
    private final SQLAuth  authDB = new SQLAuth();

    public LoginService() throws DataAccessException {
    }

    public LoginResponse login(LoginRequest loginRequest) throws DataAccessException, ServerException {
        // create the table first

        UserData userData = userDB.getUser(loginRequest.username());
        if (userData == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        if (loginRequest.password() == null)
        {
            throw new DataAccessException("Error: unauthorized");
        }
        if (!HashedPassword.checkPassWord(loginRequest.password(), userData.username()))
        {
            throw new DataAccessException("Error: unauthorized");
        }
        else
        {
            String authToken = authDB.createAuth(userData.username());
            return new LoginResponse(userData.username(), authToken);
        }
    }

}
