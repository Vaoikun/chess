package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO
{
    private static final HashSet<UserData> USER_DATA_MEMORY = new HashSet<>();
    @Override
    public void createUser(UserData u) throws DataAccessException {
        USER_DATA_MEMORY.add(u); // add the UserData into the HashSet to create in the hashSet

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {

           for (UserData singleUserData : USER_DATA_MEMORY)
           {
               if (singleUserData.username().equals(username))
               {
                   return singleUserData;
               }
           }
           return null;
    }

    @Override
    public void clear() throws DataAccessException {
        USER_DATA_MEMORY.clear();
    }
}
