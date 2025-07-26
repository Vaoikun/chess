package service;

import chess.ChessGame;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.GameMDAO;
import httprequest.CreateGameRequest;
import httprequest.JoinGameRequest;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresponse.CreateGameResponse;
import httpresponse.ListGameResponse;
import httpresponse.LoginResponse;
import httpresponse.RegisterResponse;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.ServerException;
import static org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;

public class UnitTest {
    private String authToken;
    ///test2
    private final UserService registerService1 = new UserService();
    private final UserService registerService2 = new UserService();
    private final RegisterRequest registerRequest1 = new RegisterRequest("Mole", "rat", "molerat@email.com");
    private final RegisterRequest registerRequest2 = new RegisterRequest("Dig", "hole", "dighole@email.com");
    private final UserService loginService1 = new UserService();
    private final UserService logoutService1 = new UserService();
    private final GameService createGameService1 = new GameService();
    private final GameService joinGameService1 = new GameService();
    private final GameService listGameService1 = new GameService();
    private final GameService listGameService2 = new GameService();
    private final CreateGameRequest createGameRequestA = new CreateGameRequest("GameA");
    private final CreateGameRequest createGameRequestB = new CreateGameRequest("GameB");
    private final GameMDAO gameDAO = new GameMDAO();

    public UnitTest() throws DataAccessException, SQLException {
    }

    @BeforeEach
    public void setup() throws DataAccessException, SQLException {
        gameDAO.clear();
    }

    @Test
    @Order(1)
    public void clear() throws ServerException, DataAccessException, SQLException {
        UserService clearServiceTest = new UserService();
        assertDoesNotThrow(clearServiceTest::clear);
    }

    @Test
    @Order(2)
    public void registerSuccess() throws ServerException, DataAccessException, ClientException, SQLException {
        RegisterResponse registerResponse = registerService1.register(registerRequest1);
        String username = registerResponse.username();
        assertEquals(registerResponse.username(), username);
        authToken = registerResponse.authToken();
    }

    @Test
    @Order(3)
    public void registerFailed() throws ServerException, DataAccessException, ClientException, SQLException {
        RegisterRequest missingPassword = new RegisterRequest("Mike", null, "mike@email.com");
        ClientException exception = assertThrows(ClientException.class, () -> registerService1.register(missingPassword));
        assertEquals(exception.getMessage(), "Error: must set the password.");
    }

    @Test
    @Order(4)
    public void loginSuccess() throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterResponse registerResponse = registerService1.register(new RegisterRequest("King", "Gnik", "king@email.com"));
        LoginResponse loginResponse = loginService1.login(new LoginRequest("King", "Gnik"));
        String username = registerResponse.username();
        assertEquals(username, loginResponse.username());
    }

    @Test
    @Order(5)
    public void loginFailed() throws ServerException, ClientException, DataAccessException, SQLException {
        LoginRequest loginRequest = new LoginRequest("King", null);
        ClientException exception = assertThrows(ClientException.class, () -> loginService1.login(loginRequest));
        assertEquals(exception.getMessage(), "Error: bad request.");
    }

    @Test
    @Order(6)
    public void logoutSuccess() throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("Queen", "Neeuq", "queen@email.com");
        RegisterResponse registerResponse = registerService1.register(registerRequest);
        String authToken = registerResponse.authToken();
        LoginResponse loginResponse = loginService1.login(new LoginRequest("Queen", "Neeuq"));
        assertDoesNotThrow(() -> logoutService1.logout(authToken));
    }

    @Test
    @Order(7)
    public void logoutFailed() throws ServerException, ClientException, DataAccessException, SQLException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> logoutService1.logout(null));
        assertEquals(exception.getMessage(), "Error: unauthorized.");
    }

    @Test
    @Order(8)
    public void createGameSuccess()
            throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterResponse registerResponse = registerService1.register(registerRequest2);
        String authToken = registerResponse.authToken();
        String gameName = "GameA";
        CreateGameResponse createGameResponse = createGameService1.createGame(createGameRequestA, authToken);
        GameData gameA = gameDAO.getGame(createGameResponse.gameID());
        assertEquals(gameName, gameA.gameName());
    }

    @Test
    @Order(9)
    public void createGameFailed()
            throws ServerException, ClientException, DataAccessException, SQLException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> createGameService1.createGame(createGameRequestB, null));
        assertEquals(exception.getMessage(), "Error: unauthorized.");
    }

    @Test
    @Order(10)
    public void joinGameSuccess()
            throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("Dog", "cat", "tomcat@email.com");
        RegisterResponse registerResponse = registerService1.register(registerRequest);
        String authToken = registerResponse.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest("GameC");
        CreateGameResponse createGameResponse = createGameService1.createGame(createGameRequest, authToken);
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, createGameResponse.gameID());
        assertDoesNotThrow(() -> joinGameService1.joinGame(joinGameRequest, authToken));
    }

    @Test
    @Order(11)
    public void joinGameFailed()
            throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("Car", "Train", "transport@email.com");
        RegisterResponse registerResponse = registerService1.register(registerRequest);
        String authToken = registerResponse.authToken();
        JoinGameRequest joinGameRequest = new JoinGameRequest(ChessGame.TeamColor.WHITE, 0);
        ClientException exception = assertThrows(ClientException.class, () -> joinGameService1.joinGame(joinGameRequest, authToken));
        assertEquals(exception.getMessage(), "Error: bad request.");
    }

    @Test
    @Order(12)
    public void listGamesSuccess()
            throws ServerException, ClientException, DataAccessException, SQLException {
        RegisterRequest registerRequest = new RegisterRequest("Cook", "bake", "chef@email.com");
        RegisterResponse registerResponse = registerService2.register(registerRequest);
        String authToken = registerResponse.authToken();
        ListGameResponse listGameResponse = listGameService2.listGames(authToken);
        ArrayList<GameData> gameList = listGameResponse.games();
        assertEquals(0, gameList.size());
    }

    @Test
    @Order(13)
    public void listGamesFailed()
            throws ServerException, ClientException, DataAccessException, SQLException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> listGameService1.listGames(null));
        assertEquals(exception.getMessage(), "Error: unauthorized.");
    }
}
