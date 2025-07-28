package dataaccess;

import org.mindrot.jbcrypt.BCrypt;
import server.ServerException;

public class HashPassword {
    private static String password;
    public static String hashPassword (String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String username) throws DataAccessException, ServerException {
        SQLUserDAO userDB = new SQLUserDAO();
        String hashedPassword = userDB.getUser(username).password();
        return BCrypt.checkpw(password, hashedPassword);
    }
}
