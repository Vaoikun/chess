package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static final HashSet<AuthData> AUTH_DATA_IN_MEMORY = new HashSet<>();
    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        AUTH_DATA_IN_MEMORY.add(authData);
        return authData.authToken();
    }

    @Override
    public String getAuth(String username) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_IN_MEMORY) {
            if (authData.authToken().equals(username)) {
                return authData.authToken();
            }
        }
        return null;
    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_IN_MEMORY) {
            if (authData.authToken().equals(authToken)) {
                return authData.username();
            }
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        // Better code
        AUTH_DATA_IN_MEMORY.removeIf(authData -> authData.authToken().equals(authToken));

//        for (AuthData authData : AUTH_DATA_IN_MEMORY) {
//            if (authData.authToken().equals(authToken)) {
//                AUTH_DATA_IN_MEMORY.remove(authData);
//            }
//        }
//        return null;
//    }
    }

    public void clear() throws DataAccessException {
        AUTH_DATA_IN_MEMORY.clear();
    }
}
