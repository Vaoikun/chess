package service;

import dataaccess.*;
import model.UserData;
import httpresult.RegisterResult;
import httprequest.RegisterRequest;

public class RegisterService {
    private final SQLUserDAO userDB = new SQLUserDAO();
    private final SQLAuthDAO authDB = new SQLAuthDAO();

    public RegisterService() throws DataAccessException {}

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException, ClientException, ServerException {
        SQLUserDAO.createUserTable();
        SQLAuthDAO.createAuthTable();
        UserData userData = userDB.getUser(registerRequest.username());
        if (userData != null) {
            throw new DataAccessException("Error: already taken");
        }
        if (registerRequest.password() == null){
            throw new ClientException("Error: bad request");
        }else{ // create one
            UserData newData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
            userDB.createUser(newData);
            String newAuth = authDB.createAuth(registerRequest.username());
            return new RegisterResult(registerRequest.username(), newAuth);
        }
    }
}
