package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class AuthMDAO implements AuthDAO {
    private static final HashSet<AuthData> AUTH_DATA_HASH_SET = new HashSet<>();

    public void clear() throws DataAccessException {
        AUTH_DATA_HASH_SET.clear();
    }
}
