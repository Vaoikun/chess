package dataaccess;

import model.UserData;

import java.util.HashSet;

public class UserMDAO implements UserDAO {
    private static final HashSet<UserData> USER_DATA_HASH_SET = new HashSet<>();

    @Override
    public void clear() throws DataAccessException {
        USER_DATA_HASH_SET.clear();
    }
}
