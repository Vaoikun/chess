package dataaccess;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    private static String password;
    public static String hashPassword (String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
