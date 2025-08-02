package client;

import chess.ChessGame;
import httpresponse.CreateGameResponse;
import httpresponse.LoginResponse;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.io.EOFException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() throws IOException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        ServerFacade serverFacade = new ServerFacade("http://localhost:" + port);
        ServerFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws IOException {
        ServerFacade.clear();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    @Order(1)
    public void registerSuccess() throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("King",
                "queen", "king@email.com");
        assertNotEquals(null, registerResponse.authToken());
    }

    @Test
    @Order(2)
    public void registerFail() throws IOException {
        MessageResponse messageResponse = (MessageResponse) ServerFacade.register("Rook", "knight", "rook@email.com");
        assertEquals("Error: bad request.", messageResponse.message());
    }

    @Test
    @Order(3)
    public void loginSuccess () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("Bishop", "pawn", "bishop@eamil.com");
        assertDoesNotThrow(() -> ServerFacade.login("Bishop", "pawn"));
    }

    @Test
    @Order(4)
    public void logoutSuccess () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("ABC", "CBA", "abc@email.com");
        assertDoesNotThrow(() -> ServerFacade.logout(registerResponse.authToken()));
    }

    @Test
    @Order(5)
    public void loginFail () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("Pawn", "bishop", "pawn@email.com");
        MessageResponse messageResponse = (MessageResponse) ServerFacade.login("Pawn", "bishop");
        assertEquals("Error: unauthorized.", messageResponse.message());
    }

    @Test
    @Order(6)
    public void logoutFail() throws IOException {
        MessageResponse messageResponse = (MessageResponse) ServerFacade.logout(null);
        assertEquals("Error: unauthorized.", messageResponse.message());
    }

    @Test
    @Order(7)
    public void joinGameSuccess () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("King",
                "queen", "king@email.com");
        LoginResponse loginResponse = (LoginResponse) ServerFacade.login("King", "queen");
        CreateGameResponse createGameResponse = (CreateGameResponse) ServerFacade.createGame("gameA", loginResponse.authToken());
        assertDoesNotThrow(() -> ServerFacade.joinGame(createGameResponse.gameID(), ChessGame.TeamColor.WHITE, loginResponse.authToken()));
    }

    @Test
    @Order(8)
    public void joinGameFail () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("King",
                "queen", "king@email.com");
        LoginResponse loginResponse = (LoginResponse) ServerFacade.login("King", "queen");
        CreateGameResponse createGameResponse = (CreateGameResponse) ServerFacade.createGame("gameA", loginResponse.authToken());
        MessageResponse messageResponse = (MessageResponse) ServerFacade.joinGame(createGameResponse.gameID(), ChessGame.TeamColor.WHITE, null);
        assertEquals("Error: unauthorized.", messageResponse.message());
    }

    @Test
    @Order(9)
    public void createGameSuccess() throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("King",
                "queen", "king@email.com");
        LoginResponse loginResponse = (LoginResponse) ServerFacade.login("King", "queen");
        assertDoesNotThrow(() -> ServerFacade.createGame("gameA", loginResponse.authToken()));
    }

    @Test
    @Order(10)
    public void createGameFail() throws IOException {
        MessageResponse messageResponse = (MessageResponse) ServerFacade.createGame("gameA", null);
        assertEquals("Error: unauthorized.",  messageResponse.message());
    }

    @Test
    @Order(11)
    public void listGameSuccess () throws IOException {
        RegisterResponse registerResponse = (RegisterResponse) ServerFacade.register("King",
                "queen", "king@email.com");
        LoginResponse loginResponse = (LoginResponse) ServerFacade.login("King", "queen");
        CreateGameResponse createGameResponse = (CreateGameResponse) ServerFacade.createGame("gameA", loginResponse.authToken());
        assertDoesNotThrow(() -> ServerFacade.listGames(loginResponse.authToken()));
    }

    @Test
    @Order(12)
    public void listGameFail () throws IOException {
        MessageResponse messageResponse = (MessageResponse) ServerFacade.listGames(null);
        assertEquals("Error: unauthorized.", messageResponse.message());
    }
}
