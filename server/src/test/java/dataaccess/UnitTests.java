package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {

    // before each test we can clean up all tables to not affect other tests

    private final SQLUser sqlUserRefer = new SQLUser();
    private final SQLAuth sqlAuthRefer = new SQLAuth();
    private final SQLGame sqlGameRefer = new SQLGame();

    private final UserData userData = new UserData("Serge", "Serge111", "sjh66@byu.edu");
    private final UserData userDataFailed = new UserData("", "Serge", "sjh66@byu.edu");

    public UnitTests() throws DataAccessException {
    }

    @BeforeEach
    public void createDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        SQLUser.createUserTable();
        SQLAuth.createAuthTable();
        SQLGame.createGamesTable();


    }

    @BeforeEach
    @Test
    public void clear() throws DataAccessException {
        sqlUserRefer.clear();
        sqlAuthRefer.clear();
        sqlGameRefer.clear();
        assertDoesNotThrow(sqlUserRefer::clear);
        assertDoesNotThrow(sqlAuthRefer::clear);
        assertDoesNotThrow(sqlGameRefer::clear);
    }

    @Test
    @Order(1)
    public void registerSuccess() throws DataAccessException {
        assertDoesNotThrow(() -> sqlUserRefer.createUser(userData));
    }

    @Test
    @Order(2)
    public void registerFailed() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        assertThrows(DataAccessException.class, () -> sqlUserRefer.createUser(userData));
    }

    @Test
    @Order(3)
    public void loginSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        assertEquals(userData.username(), returnedUserData.username());
    }

    @Test
    @Order(4)
    public void loginFailed() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        assertNotEquals(returnedUserData, userDataFailed);
    }

    @Test
    @Order(5)
    public void createGameSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        assertDoesNotThrow(() -> sqlGameRefer.createGame("game1"));
    }

    @Test
    @Order(6)
    public void createGameFailed() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        assertThrows(DataAccessException.class, () -> sqlGameRefer.createGame(null));
    }

    @Test
    @Order(7)
    public void listGameSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        String authToken = sqlAuthRefer.createAuth(returnedUserData.username());
        assertDoesNotThrow(() -> sqlGameRefer.listGames(authToken));
    }

    @Test
    @Order(8)
    public void listGameFailed() throws DataAccessException
    {
        DataAccessException dataAccessException =  assertThrows(DataAccessException.class, () -> sqlGameRefer.listGames(null));
        assertEquals("AuthToken is null", dataAccessException.getMessage());
    }

    @Test
    @Order(9)
    public void joinGameSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        int gameID = sqlGameRefer.createGame("game1");
        assertDoesNotThrow(() -> sqlGameRefer.joinGame(gameID, ChessGame.TeamColor.WHITE, returnedUserData.username()));
    }

    @Test
    @Order(10)
    public void joinGameFailed() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        int gameID = sqlGameRefer.createGame("game1");
        DataAccessException dataAccessException =  assertThrows(DataAccessException.class, () -> sqlGameRefer.joinGame(gameID, ChessGame.TeamColor.WHITE, null));
        assertEquals("Your username is null", dataAccessException.getMessage());
    }

    @Test
    @Order(11)
    public void logoutSuccess() throws DataAccessException
    {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        String authToken = sqlAuthRefer.createAuth(returnedUserData.username());
        assertDoesNotThrow(() -> sqlAuthRefer.deleteAuth(authToken));
    }

    @Test
    @Order(12)
    public void logoutFailed()
    {
        DataAccessException dataAccessException = assertThrows(DataAccessException.class, () -> sqlAuthRefer.deleteAuth(null));
        assertEquals("AuthToken is null", dataAccessException.getMessage());
    }

    @Test
    @Order(13)
    public void updateGameSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        int gameID =  sqlGameRefer.createGame("game2");
        GameData returnedGame = sqlGameRefer.getGame(gameID);
        assertDoesNotThrow(() ->  sqlGameRefer.updateGame(returnedUserData.username(), ChessGame.TeamColor.WHITE, returnedGame));
    }

    @Test
    @Order(14)
    public void updateGameFailed() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        int gameID =  sqlGameRefer.createGame("game2");
        GameData returnedGame = sqlGameRefer.getGame(gameID);
       sqlGameRefer.updateGame("Bob", ChessGame.TeamColor.WHITE, returnedGame);
        assertNotEquals(userData.username(), "Bob");

    }

    @Test
    @Order(15)
    public void createAuthSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        UserData returnedUserData = sqlUserRefer.getUser(userData.username());
        assertDoesNotThrow(() ->sqlAuthRefer.createAuth(returnedUserData.username()));
    }

    @Test
    @Order(16)
    public void createAuthFailed()
    {
        assertThrows(DataAccessException.class, () ->sqlAuthRefer.createAuth(null));
    }

    @Test
    @Order(17)
    public void getGameSuccess() throws DataAccessException {
        sqlUserRefer.createUser(userData);
        int gameID = sqlGameRefer.createGame("game1");
        assertDoesNotThrow(() -> sqlGameRefer.getGame(gameID));
    }

    @Test
    @Order(18)
    public void getGameFailed()
    {
        assertThrows(DataAccessException.class, () -> sqlGameRefer.getGame(100000));
    }

}
