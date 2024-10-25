package dataaccess;

import model.AuthData;

import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private static final HashSet<AuthData> AUTH_DATA_IN_MEMORY = new HashSet<>();

    /**
     * @param username;
     * @return authToken
     * @throws DataAccessException;
     */
    @Override
    public String createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, username);
        AUTH_DATA_IN_MEMORY.add(authData);
        return authData.authToken();
    }

    /**
     * @param username;
     * @return authToken
     * @throws DataAccessException;
     */
    @Override
    public String getAuth(String username) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_IN_MEMORY) {
            if (authData.authToken().equals(username)) {
                return authData.authToken();
            }
        }
        return null;
    }

    /**
     * @param authToken;
     * @return username;
     * @throws DataAccessException;
     */
    @Override
    public String getUsername(String authToken) throws DataAccessException {
        for (AuthData authData : AUTH_DATA_IN_MEMORY) {
            if (authData.authToken().equals(authToken)) {
                return authData.username();
            }
        }
        return null;
    }

    /**
     * @param authToken;
     * @throws DataAccessException;
     */
    public void deleteAuth(String authToken) throws DataAccessException {
        AUTH_DATA_IN_MEMORY.removeIf(authData -> authData.authToken().equals(authToken));
    }

    /**
     * @throws DataAccessException;
     */
    public void clear() throws DataAccessException {
        AUTH_DATA_IN_MEMORY.clear();
    }
}
