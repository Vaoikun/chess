package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    private final SQLUserDAO SQLUserDB = new SQLUserDAO();
    private final SQLGameDAO SQLGameDB = new SQLGameDAO();
    private final SQLAuthDAO SQLAuthDB = new SQLAuthDAO();

    private final UserData userData = new UserData("Wolf", "coyote", "wolf@email.com");

    public UnitTest() throws DataAccessException {
    }

    @BeforeEach
    public void createDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        SQLUserDAO.createUserTable();
        SQLAuthDAO.createAuthTable();
        SQLGameDAO.createGamesTable();
    }

    @AfterEach
    public void clear() throws DataAccessException {
        SQLUserDB.clear();
        SQLAuthDB.clear();
        SQLGameDB.clear();
    }

    @Test
    @Order(1)
    public void clearTest() throws DataAccessException {
        SQLUserDB.clear();
        SQLAuthDB.clear();
        SQLGameDB.clear();
        assertDoesNotThrow(SQLUserDB::clear);
        assertDoesNotThrow(SQLAuthDB::clear);
        assertDoesNotThrow(SQLGameDB::clear);
    }

    @Test
    @Order(2)
    public void registerSuccess() throws DataAccessException {
        assertDoesNotThrow(() -> SQLUserDB.createUser(userData));
    }

    @Test
    @Order(3)
    public void loginSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        assertEquals(userData.username(), returnedData.username());
    }

    @Test
    @Order(4)
    public void createGameSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        assertDoesNotThrow(() -> SQLGameDB.createGame("game1"));

    }

    @Test
    @Order(5)
    public void listGameSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        String authToken = SQLAuthDB.createAuth(returnedData.username());
        assertDoesNotThrow(() -> SQLGameDB.listGames(authToken));
    }

    @Test
    @Order(6)
    public void joinGameSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        int gameID = SQLGameDB.createGame("game1");
        assertDoesNotThrow(() -> SQLGameDB.joinGame(gameID, ChessGame.TeamColor.WHITE, returnedData.username()));
    }

    @Test
    @Order(7)
    public void updateGameSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        int gameID = SQLGameDB.createGame("game1");
        GameData returnedGame = SQLGameDB.getGame(gameID);
        assertDoesNotThrow(() -> SQLGameDB.updateGame(returnedData.username(), ChessGame.TeamColor.WHITE, returnedGame));

    }

    @Test
    @Order(8)
    public void logoutSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        String authToken = SQLAuthDB.createAuth(returnedData.username());
        assertDoesNotThrow(() -> SQLAuthDB.deleteAuth(authToken));
    }

    @Test
    @Order(9)
    public void createAuthSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnData = SQLUserDB.getUser(userData.username());
        assertDoesNotThrow(() -> SQLAuthDB.createAuth(returnData.username()));
    }

    @Test
    @Order(10)
    public void getGameSuccess() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        int gameID = SQLGameDB.createGame("game1");
        assertDoesNotThrow(() -> SQLGameDB.getGame(gameID));
    }

    @Test
    @Order(11)
    public void registerFailed() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        assertThrows(DataAccessException.class, () -> SQLUserDB.createUser(userData));
    }

    @Test
    @Order(12)
    public void loginFailed() throws DataAccessException, SQLException {
        UserData wrongUserData = new UserData("Golf", "mayo", "golf@email.com");
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        assertNotEquals(returnedData, wrongUserData);
    }

    @Test
    @Order(13)
    public void createGameFailed() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        assertThrows(DataAccessException.class, () -> SQLGameDB.createGame(null));
    }

    @Test
    @Order(14)
    public void listGameFailed() throws DataAccessException, SQLException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> SQLGameDB.listGames(null));
        assertEquals("Error: null authToken.", exception.getMessage());
    }

    @Test
    @Order(15)
    public void joinGameFailed() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        int gameID = SQLGameDB.createGame("game1");
        DataAccessException exception = assertThrows(DataAccessException.class, () -> SQLGameDB.joinGame(gameID, ChessGame.TeamColor.WHITE, null));
        assertEquals("null username.", exception.getMessage());
    }

    @Test
    @Order(16)
    public void logoutFailed() throws DataAccessException, SQLException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> SQLAuthDB.deleteAuth(null));
        assertEquals("null authToken.", exception.getMessage());
    }

    @Test
    @Order(17)
    public void updateGameFailed() throws DataAccessException, SQLException {
        SQLUserDB.createUser(userData);
        UserData returnedData = SQLUserDB.getUser(userData.username());
        int gameID = SQLGameDB.createGame("game1");
        GameData returnedGame = SQLGameDB.getGame(gameID);
        SQLGameDB.updateGame("Rook", ChessGame.TeamColor.WHITE, returnedGame);
        assertNotEquals(userData.username(), "Rook");
    }

    @Test
    @Order(18)
    public void getGameFailed() throws DataAccessException, SQLException {
        assertThrows(DataAccessException.class, () -> SQLGameDB.getGame(00000));
    }

    @Test
    @Order(19)
    public void createAuthFailed() throws DataAccessException, SQLException {
        assertThrows(DataAccessException.class, () -> SQLAuthDB.createAuth(null));
    }
}
