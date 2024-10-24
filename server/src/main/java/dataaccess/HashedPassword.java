package dataaccess;

import org.mindrot.jbcrypt.BCrypt;

public class HashedPassword {
    private static String password;
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String username) throws DataAccessException {
        MemoryUserDAO memoryUser = new MemoryUserDAO();
        String hashedPassword = hashPassword(memoryUser.getUser(username).password());
        return BCrypt.checkpw(password, hashedPassword);
    }



}
