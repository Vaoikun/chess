package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private static final HashSet<UserData> USER_DATA_MEMORY = new HashSet<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        USER_DATA_MEMORY.add(userData);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData userData : USER_DATA_MEMORY) {
            if (userData.username().equals(username)) {
                return userData;
            }
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        USER_DATA_MEMORY.clear();
    }
}
