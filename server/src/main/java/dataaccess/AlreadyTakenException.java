package dataaccess;

/**
 * Indicates there was an error creating user
 */
public class AlreadyTakenException extends Exception {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
