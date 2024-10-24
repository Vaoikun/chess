package service;

import httprequest.CreateGameRequest;
import httprequest.JoinGameRequest;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresult.CreateGameResult;
import httpresult.ListGameResult;
import httpresult.LoginResult;
import httpresult.RegisterResult;
import model.GameData;
import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class UnitTest
{
    private String authToken_aoi;
    private String authToken_vaoi;
    private final CreateGameRequest createGameRequestTest = new CreateGameRequest("Game1");
    private final CreateGameRequest createGameRequestTest2 = new CreateGameRequest("Game2");
    private final LoginRequest loginRequest = new LoginRequest("aoi", "aoi123");
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private final MemoryUserDAO userDAO = new MemoryUserDAO();
    private final MemoryGameDAO gameDAO = new MemoryGameDAO();
    private final RegisterService registerServiceTest = new RegisterService();
    private final LoginService loginServiceTest = new LoginService();
    private final CreateGameService createGameServiceTest = new CreateGameService();
    private final JoinGameService joinGameServiceTest = new JoinGameService();
    private final ListGamesService listGamesServiceTest = new ListGamesService();
    private final LogoutService logoutServiceTest = new LogoutService();
    private final RegisterRequest registerRequestTest = new RegisterRequest("aoi", "aoi123", "vaoikun@byu.edu");
    private final RegisterRequest registerRequestTest1 = new RegisterRequest("Rob", "rob321", "robby@byu.edu");
    private final RegisterRequest registerRequestTest2 = new RegisterRequest("Cob", "cob000", "cobby@byu.edu");

    public UnitTest() throws DataAccessException {
    }

    @Test
    @Order(1)
    public void clear() throws ServerException, DataAccessException {
        ClearService clearServiceTest = new ClearService();
        assertDoesNotThrow(clearServiceTest::clear);
    }

    @Test
    @Order(2)
    public void registerSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterResult registerResultTest = registerServiceTest.register(registerRequestTest);
        String username = registerResultTest.username();
        assertEquals(registerResultTest.username(), username);
        authToken_aoi = registerResultTest.authToken();
    }

    @Test
    @Order(3)
    public void registerFailed() throws ServerException, ClientException, DataAccessException {
        RegisterRequest missingPassword = new RegisterRequest("johncena", null, "vaoikun@byu.edu");
        ClientException clientException = assertThrows(ClientException.class, () ->  registerServiceTest.register(missingPassword));
        assertEquals(clientException.getMessage(), "Error: bad request");
    }

    @Test
    @Order(4)
    public void loginSuccess() throws ServerException, DataAccessException, ClientException {
        RegisterResult registerResultTest = registerServiceTest.register(new RegisterRequest("vaoi", "321", "vaoiwilliams@gmail.com"));
        LoginResult loginResult = loginServiceTest.login(new LoginRequest("vaoi", "321"));
        String username = registerResultTest.username();
        assertEquals(username, loginResult.username());
    }

    @Test
    @Order(5)
    public void loginFailed() throws ServerException, DataAccessException, ClientException {
        LoginRequest loginBadRequestTest = new LoginRequest("vaoi", null);
        DataAccessException daException = assertThrows(DataAccessException.class, () ->  loginServiceTest.login(loginBadRequestTest));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }

    @Test
    @Order(6)
    public void createGameSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterResult registerResultRob =  registerServiceTest.register(registerRequestTest1);
        String authToken = registerResultRob.authToken();
        String gameName = "Game1";
        CreateGameResult createGameResultTest = createGameServiceTest.createGame(createGameRequestTest, authToken);
        GameData game1 = gameDAO.getGame(createGameResultTest.gameID());
        assertEquals(gameName, game1.gameName());
    }

    @Test
    @Order(7)
    public void createGameFailed() {
        DataAccessException daException = assertThrows(DataAccessException.class, () ->  createGameServiceTest.createGame(createGameRequestTest2, null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }


    @Test
    @Order(8)
    public void listGameSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestKai = new RegisterRequest("Kai", "456", "kai@byu.edu");
        RegisterResult registerResult = registerServiceTest.register(registerRequestKai);
        String authToken = registerResult.authToken();
        ListGameResult listGameResultRob = listGamesServiceTest.listGame(authToken);
        ArrayList<GameData> games = listGameResultRob.gameData();
        assertEquals(0, games.size());
    }

    @Test
    @Order(9)
    public void listGameFailed() {
        DataAccessException daException = assertThrows(DataAccessException.class, () -> listGamesServiceTest.listGame(null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }

    @Test
    @Order(10)
    public void joinGameSuccess() throws ServerException, ClientException, DataAccessException, AlreadyTakenException {
        RegisterRequest registerRequestKai = new RegisterRequest("John", "456", "john@byu.edu");
        RegisterResult registerResult = registerServiceTest.register(registerRequestKai);
        String authToken = registerResult.authToken();
        CreateGameRequest createGameRequestJohn = new CreateGameRequest("game3");
        CreateGameResult createGameResultTest = createGameServiceTest.createGame(createGameRequestJohn, authToken);
        JoinGameRequest joinGameRequestJohn = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResultTest.gameID());
        assertDoesNotThrow(() -> joinGameServiceTest.joinGame(joinGameRequestJohn, authToken));
    }

    @Test
    @Order(11)
    public void joinGameFailed() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestMike = new RegisterRequest("Mike", "456", "mike@byu.edu");
        RegisterResult registerResult = registerServiceTest.register(registerRequestMike);
        String authToken = registerResult.authToken();
        JoinGameRequest joinGameRequestMike = new JoinGameRequest(ChessGame.TeamColor.WHITE, 0);
        ClientException clientException = assertThrows(ClientException.class, () -> joinGameServiceTest.joinGame(joinGameRequestMike, authToken));
        assertEquals(clientException.getMessage(), "Error: bad request");
    }

    @Test
    @Order(12)
    public void logoutSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestJosha = new RegisterRequest("Josh", "111", "josh@byu.edu");
        RegisterResult registerResult = registerServiceTest.register(registerRequestJosha);
        String authToken = registerResult.authToken();
        LoginResult loginResult = loginServiceTest.login(new LoginRequest("Josh", "111"));
        assertDoesNotThrow(() ->  logoutServiceTest.logout(authToken));
    }

    @Test
    @Order(13)
    public void logoutFailed() throws ServerException, ClientException, DataAccessException {
        DataAccessException daException = assertThrows(DataAccessException.class, () -> logoutServiceTest.logout(null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }


}
