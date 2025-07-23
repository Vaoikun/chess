package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class AuthMDAO implements AuthDAO {
    private static final HashSet<AuthData> AUTH_DATA_HASH_SET = new HashSet<>();

    @Override
    public void clear() throws DataAccessException {
        AUTH_DATA_HASH_SET.clear();
    }

    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        AUTH_DATA_HASH_SET.add(authData);
        return authData.authToken();
    }

    @Override
    public String getAuth(String username) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_HASH_SET) {
            if (authData.authToken().equals(username)){
                return authData.authToken();
            }
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        AUTH_DATA_HASH_SET.removeIf(authData -> authData.authToken().equals(authToken));
    }

    public String getUsername(String authToken) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_HASH_SET) {
            if (authData.authToken().equals(authToken)){
                return authData.username();
            }
        }
        return null;
    }
}
