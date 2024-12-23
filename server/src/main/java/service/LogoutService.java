package service;

import dataaccess.*;

public class LogoutService {
    SQLAuthDAO authDB = new SQLAuthDAO();

    public LogoutService() throws DataAccessException {}

    public void logout(String authToken) throws DataAccessException, ServerException {
        String username = authDB.getAuth(authToken);
        if (username == null) {
            throw new DataAccessException("Error: unauthorized");
        }else{
            authDB.deleteAuth(authToken);
        }
    }
}
