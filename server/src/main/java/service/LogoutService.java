package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.ServerException;
import dataaccess.SQLAuth;

public class LogoutService
{
    SQLAuth authDB = new SQLAuth();

    public LogoutService() throws DataAccessException {
    }


    public void logout (String authToken) throws DataAccessException, ServerException {
       String username = authDB.getAuth(authToken);
       if (username == null)
       {
           throw new DataAccessException("Error: unauthorized");
       }
       else
       {
           authDB.deleteAuth(authToken);
       }
    }

}
