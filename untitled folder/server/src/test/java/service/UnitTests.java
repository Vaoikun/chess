package service;

import httprequest.CreateGameRequest;
import httprequest.JoinGameRequest;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresponse.CreateGameResponse;
import httpresponse.LIstGameResponse;
import httpresponse.LoginResponse;
import httpresponse.RegisterResponse;
import model.GameData;
import chess.ChessGame;
import dataaccess.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class UnitTests
{
    private String authTokenForSerge;
    private String authTokenForSu;
    private final CreateGameRequest createGameRequestTest = new CreateGameRequest("Game1");

    private final CreateGameRequest createGameRequestTest2 = new CreateGameRequest("Game2");
    private final LoginRequest loginRequest = new LoginRequest("Serge", "Serge666");
    private final MemoryAuthDAO authDAO = new MemoryAuthDAO();

    private final MemoryUserDAO userDAO = new MemoryUserDAO();

    private final MemoryGameDAO gameDAO = new MemoryGameDAO();

    private final RegisterService registerServiceTest = new RegisterService();
    private final LoginService loginServiceTest = new LoginService();

    private final CreateGameService createGameServiceTest = new CreateGameService();
    private final JoinGameService joinGameServiceTest = new JoinGameService();
    private final ListGamesService listGamesServiceTest = new ListGamesService();
    private final LogoutService logoutServiceTest = new LogoutService();


    private final RegisterRequest registerRequestTest = new RegisterRequest("Serge", "Serge666", "sjh666@byu.edu");
    private final RegisterRequest registerRequestTest1 = new RegisterRequest("Bob", "bob666", "bob@byu.edu");

    private final RegisterRequest registerRequestTest2 = new RegisterRequest("CC", "cc666", "cc@byu.edu");

    public UnitTests() throws DataAccessException {
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
        RegisterResponse registerResponseTest = registerServiceTest.register(registerRequestTest);
        String username = registerResponseTest.username();
        assertEquals(registerResponseTest.username(), username);
        authTokenForSerge = registerResponseTest.authToken();
    }

    @Test
    @Order(3)
    public void registerFailed() throws ServerException, ClientException, DataAccessException {
        RegisterRequest missingPassword = new RegisterRequest("lala", null, "sjh66@byu.edu");
        ClientException clientException = assertThrows(ClientException.class, () ->  registerServiceTest.register(missingPassword));
        assertEquals(clientException.getMessage(), "Error: bad request");
    }

    @Test
    @Order(4)
    public void loginSuccess() throws ServerException, DataAccessException, ClientException {
       RegisterResponse registerResponseTest = registerServiceTest.register(new RegisterRequest("Su", "123", "Junhao.S@Outlook.com"));
       LoginResponse loginResponse = loginServiceTest.login(new LoginRequest("Su", "123"));
       String username = registerResponseTest.username();
       assertEquals(username, loginResponse.username());
    }

    @Test
    @Order(5)
    public void loginFailed() throws ServerException, DataAccessException, ClientException {
        LoginRequest loginBadRequestTest = new LoginRequest("Su", null);
        DataAccessException daException = assertThrows(DataAccessException.class, () ->  loginServiceTest.login(loginBadRequestTest));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }

    @Test
    @Order(6)
    public void createGameSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterResponse registerResponseBob =  registerServiceTest.register(registerRequestTest1);
        String authToken = registerResponseBob.authToken();
        String gameName = "Game1";
        CreateGameResponse createGameResponseTest = createGameServiceTest.createGame(createGameRequestTest, authToken);
        GameData game1 = gameDAO.getGame(createGameResponseTest.gameID());
        assertEquals(gameName, game1.gameName());
    }

    @Test
    @Order(7)
    public void createGameFailed()
    {
        DataAccessException daException = assertThrows(DataAccessException.class, () ->  createGameServiceTest.createGame(createGameRequestTest2, null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }


    @Test
    @Order(8)
    public void listGameSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestVictor = new RegisterRequest("Victor", "111", "victor@byu.edu");
        RegisterResponse registerResponse = registerServiceTest.register(registerRequestVictor);
        String authToken = registerResponse.authToken();
        LIstGameResponse lIstGameResponseBob = listGamesServiceTest.listGame(authToken);
        ArrayList<GameData> games = lIstGameResponseBob.games();
        assertEquals(0, games.size());
    }

    @Test
    @Order(9)
    public void listGameFailed()
    {
        DataAccessException daException = assertThrows(DataAccessException.class, () -> listGamesServiceTest.listGame(null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }

    @Test
    @Order(10)
    public void joinGameSuccess() throws ServerException, ClientException, DataAccessException, AlreadyTakenException {
        RegisterRequest registerRequestVictor = new RegisterRequest("Tom", "111", "tom@byu.edu");
        RegisterResponse registerResponse = registerServiceTest.register(registerRequestVictor);
        String authToken = registerResponse.authToken();

        CreateGameRequest createGameRequestTom = new CreateGameRequest("game3");
        CreateGameResponse createGameResponseTest = createGameServiceTest.createGame(createGameRequestTom, authToken);

        JoinGameRequest joinGameRequestTom = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResponseTest.gameID());
        assertDoesNotThrow(() -> joinGameServiceTest.joinGame(joinGameRequestTom, authToken));
    }

    @Test
    @Order(11)
    public void joinGameFailed() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestJack = new RegisterRequest("Jack", "111", "jack@byu.edu");
        RegisterResponse registerResponse = registerServiceTest.register(registerRequestJack);
        String authToken = registerResponse.authToken();

        JoinGameRequest joinGameRequestJack = new JoinGameRequest(ChessGame.TeamColor.WHITE, 0);

        ClientException clientException = assertThrows(ClientException.class, () -> joinGameServiceTest.joinGame(joinGameRequestJack, authToken));
        assertEquals(clientException.getMessage(), "Error: bad request");
    }

    @Test
    @Order(12)
    public void logoutSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequestJosha = new RegisterRequest("Josha", "111", "josha@byu.edu");
        RegisterResponse registerResponse = registerServiceTest.register(registerRequestJosha);
        String authToken = registerResponse.authToken();
        LoginResponse loginResponse = loginServiceTest.login(new LoginRequest("Josha", "111"));
        assertDoesNotThrow(() ->  logoutServiceTest.logout(authToken));
    }

    @Test
    @Order(13)
    public void logoutFailed() throws ServerException, ClientException, DataAccessException {
        DataAccessException daException = assertThrows(DataAccessException.class, () -> logoutServiceTest.logout(null));
        assertEquals(daException.getMessage(), "Error: unauthorized");
    }







}
