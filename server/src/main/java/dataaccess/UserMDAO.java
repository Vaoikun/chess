package dataaccess;

import model.UserData;

import java.util.HashSet;

public class UserMDAO implements UserDAO {
    private static final HashSet<UserData> USER_DATA_HASH_SET = new HashSet<>();

    @Override
    public void clear() throws DataAccessException {
        USER_DATA_HASH_SET.clear();
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        USER_DATA_HASH_SET.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData userData : USER_DATA_HASH_SET) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }
}
