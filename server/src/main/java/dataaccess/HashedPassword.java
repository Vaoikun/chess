package dataaccess;
import org.mindrot.jbcrypt.BCrypt;
public class HashedPassword
{
    private static String normalPassword;

    public static String hashPassword(String normalPassword)
    {
        return BCrypt.hashpw(normalPassword, BCrypt.gensalt());
    }

    public static boolean checkPassWord(String normalPassword, String usernameInDB) throws DataAccessException {
        SQLUser sqlUserRefer = new SQLUser();
        String hashedPasswordInDB = sqlUserRefer.getUser(usernameInDB).password();
        return BCrypt.checkpw(normalPassword,hashedPasswordInDB);


    }

}
