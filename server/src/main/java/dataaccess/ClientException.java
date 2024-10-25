package dataaccess;

/**
 * Indicates there was an error on Client's side
 */
public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }
}
