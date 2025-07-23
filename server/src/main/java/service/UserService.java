package service;

import dataaccess.*;
import httprequest.CreateGameRequest;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresponse.CreateGameResponse;
import httpresponse.LoginResponse;
import httpresponse.RegisterResponse;
import model.UserData;
import server.ServerException;

public class UserService {

    private final GameMDAO gameDB = new GameMDAO();
    private final UserMDAO userDB = new UserMDAO();
    private final AuthMDAO authDB = new AuthMDAO();

    public UserService() throws DataAccessException {}

    public void clear() throws DataAccessException, ServerException {
        gameDB.clear();
        userDB.clear();
        authDB.clear();
    }

    public RegisterResponse register(RegisterRequest request)
            throws DataAccessException, ServerException {
        UserData userData = userDB.getUser(request.username());
        if (userData != null) {
            throw new DataAccessException("Error: username already taken.");
        }
        if (request.password() == null) {
            throw new ClientException("Error: must set the password.");
        } else {
            UserData newUser = new UserData(request.username(), request.password(), request.email());
            userDB.createUser(newUser);
            String newAuth = authDB.createAuth(request.username());
            return new RegisterResponse(request.username(), newAuth);
        }
    }

    public LoginResponse login(LoginRequest loginRequest)
            throws DataAccessException, ServerException, ClientException {
        UserData userData = userDB.getUser(loginRequest.username());
        if (userData == null) {
            throw new DataAccessException("Error: user doesn't exist.");
        }
        if (userData.username() == null || loginRequest.password() == null) {
            throw new ClientException("Error: bad request.");
        }
        if (!loginRequest.password().equals(userData.password())){
            throw new DataAccessException("Error: unauthorized.");
        } else {
            String authToken = authDB.createAuth(userData.username());
            return new LoginResponse(userData.username(), authToken);
        }
    }

    public void logout(String authToken) throws DataAccessException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null){
            throw new DataAccessException("Error: unauthorized.");
        } else {
            authDB.deleteAuth(authToken);
        }
    }
}
