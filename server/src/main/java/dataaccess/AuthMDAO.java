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
}
