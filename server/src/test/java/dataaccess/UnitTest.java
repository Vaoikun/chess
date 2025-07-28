package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.ServerException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    private final SQLUserDAO sQLUserDB = new SQLUserDAO();
    private final SQLGameDAO sQLGameDB = new SQLGameDAO();
    private final SQLAuthDAO sQLAuthDB = new SQLAuthDAO();

    private final UserData userData = new UserData("Wolf", "coyote", "wolf@email.com");

    public UnitTest() throws DataAccessException, ServerException {
    }

    @BeforeEach
    public void createDB() throws DataAccessException, ServerException {
        DatabaseManager.createDatabase();
        SQLUserDAO.createUserTable();
        SQLAuthDAO.createAuthTable();
        SQLGameDAO.createGamesTable();
    }

    @AfterEach
    public void clear() throws DataAccessException, ServerException {
        sQLUserDB.clear();
        sQLAuthDB.clear();
        sQLGameDB.clear();
    }

    @Test
    @Order(1)
    public void clearTest() throws DataAccessException, ServerException {
        sQLUserDB.clear();
        sQLAuthDB.clear();
        sQLGameDB.clear();
        assertDoesNotThrow(sQLUserDB::clear);
        assertDoesNotThrow(sQLAuthDB::clear);
        assertDoesNotThrow(sQLGameDB::clear);
    }

    @Test
    @Order(2)
    public void registerSuccess() throws DataAccessException, ServerException {
        assertDoesNotThrow(() -> sQLUserDB.createUser(userData));
    }

    @Test
    @Order(3)
    public void loginSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        assertEquals(userData.username(), returnedData.username());
    }

    @Test
    @Order(4)
    public void createGameSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        assertDoesNotThrow(() -> sQLGameDB.createGame("game1"));

    }

    @Test
    @Order(5)
    public void listGameSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        String authToken = sQLAuthDB.createAuth(returnedData.username());
        assertDoesNotThrow(() -> sQLGameDB.listGames(authToken));
    }

    @Test
    @Order(6)
    public void joinGameSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        int gameID = sQLGameDB.createGame("game1");
        assertDoesNotThrow(() -> sQLGameDB.joinGame(gameID, ChessGame.TeamColor.WHITE, returnedData.username()));
    }

    @Test
    @Order(7)
    public void updateGameSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        int gameID = sQLGameDB.createGame("game1");
        GameData returnedGame = sQLGameDB.getGame(gameID);
        assertDoesNotThrow(() -> sQLGameDB.updateGame(returnedData.username(), ChessGame.TeamColor.WHITE, returnedGame));

    }

    @Test
    @Order(8)
    public void logoutSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        String authToken = sQLAuthDB.createAuth(returnedData.username());
        assertDoesNotThrow(() -> sQLAuthDB.deleteAuth(authToken));
    }

    @Test
    @Order(9)
    public void createAuthSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnData = sQLUserDB.getUser(userData.username());
        assertDoesNotThrow(() -> sQLAuthDB.createAuth(returnData.username()));
    }

    @Test
    @Order(10)
    public void getGameSuccess() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        int gameID = sQLGameDB.createGame("game1");
        assertDoesNotThrow(() -> sQLGameDB.getGame(gameID));
    }

    @Test
    @Order(11)
    public void registerFailed() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        assertThrows(DataAccessException.class, () -> sQLUserDB.createUser(userData));
    }

    @Test
    @Order(12)
    public void loginFailed() throws DataAccessException, SQLException, ServerException {
        UserData wrongUserData = new UserData("Golf", "mayo", "golf@email.com");
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        assertNotEquals(returnedData, wrongUserData);
    }

    @Test
    @Order(13)
    public void createGameFailed() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        assertThrows(DataAccessException.class, () -> sQLGameDB.createGame(null));
    }

    @Test
    @Order(14)
    public void listGameFailed() throws DataAccessException, SQLException, ServerException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> sQLGameDB.listGames(null));
        assertEquals("Error: null authToken.", exception.getMessage());
    }

    @Test
    @Order(15)
    public void joinGameFailed() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        int gameID = sQLGameDB.createGame("game1");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> sQLGameDB.joinGame(gameID, ChessGame.TeamColor.WHITE, null));
        assertEquals("Error: null username.", exception.getMessage());
    }

    @Test
    @Order(16)
    public void logoutFailed() throws DataAccessException, SQLException, ServerException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> sQLAuthDB.deleteAuth(null));
        assertEquals("Error: null authToken.", exception.getMessage());
    }

    @Test
    @Order(17)
    public void updateGameFailed() throws DataAccessException, SQLException, ServerException {
        sQLUserDB.createUser(userData);
        UserData returnedData = sQLUserDB.getUser(userData.username());
        int gameID = sQLGameDB.createGame("game1");
        GameData returnedGame = sQLGameDB.getGame(gameID);
        sQLGameDB.updateGame("Rook", ChessGame.TeamColor.WHITE, returnedGame);
        assertNotEquals(userData.username(), "Rook");
    }

    @Test
    @Order(18)
    public void getGameFailed() throws DataAccessException, SQLException, ServerException {
        assertThrows(DataAccessException.class, () -> sQLGameDB.getGame(000000));
    }

    @Test
    @Order(19)
    public void createAuthFailed() throws DataAccessException, SQLException, ServerException {
        assertThrows(DataAccessException.class, () -> sQLAuthDB.createAuth(null));
    }
}
