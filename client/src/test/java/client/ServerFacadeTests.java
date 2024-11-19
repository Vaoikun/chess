package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import chess.ChessGame;
import httpresult.CreateGameResult;
import httpresult.LoginResult;
import httpresult.MessageResult;
import httpresult.RegisterResult;

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

    @BeforeEach
    public void clear() throws IOException {
        ServerFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    public void registerSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        assertNotEquals(null, registerResponse.authToken());
    }

    @Test
    @Order(2)
    public void registerFailed() throws IOException {
        MessageResult messageResponse = (MessageResult) ServerFacade.register("Joan", null, "jjj@byu.edu");
        assertEquals("Error: bad request", messageResponse.message());
    }

    @Test
    @Order(3)
    public void loginSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        assertDoesNotThrow(() -> ServerFacade.login("Joan", "joanPassword"));
    }

    @Test
    @Order(4)
    public void loginFailed() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        MessageResult messageResponse = (MessageResult) ServerFacade.login("Joan", null);
        assertEquals("Error: unauthorized", messageResponse.message());
    }

    @Test
    @Order(5)
    public void createGameSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        LoginResult loginResponse = (LoginResult) ServerFacade.login("Joan", "joanPassword");
        assertDoesNotThrow(() -> ServerFacade.createGame("game1", loginResponse.authToken()));
    }

    @Test
    @Order(6)
    public void createGameFailed() throws IOException {
        MessageResult messageResponse = (MessageResult) ServerFacade.createGame("game1", null);
        assertEquals("Error: unauthorized", messageResponse.message());
    }

    @Test
    @Order(7)
    public void joinGameSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        LoginResult loginResponse = (LoginResult) ServerFacade.login("Joan", "joanPassword");
        CreateGameResult createGameResponse = (CreateGameResult) ServerFacade.createGame("game1", loginResponse.authToken());
        assertDoesNotThrow(() -> ServerFacade.joinGame(ChessGame.TeamColor.WHITE, createGameResponse.gameID(), loginResponse.authToken()));
    }

    @Test
    @Order(8)
    public void joinGameFailed() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        LoginResult loginResponse = (LoginResult) ServerFacade.login("Joan", "joanPassword");
        CreateGameResult createGameResponse = (CreateGameResult) ServerFacade.createGame("game1", loginResponse.authToken());
        MessageResult messageResponse = (MessageResult) ServerFacade.joinGame(ChessGame.TeamColor.WHITE, createGameResponse.gameID(), null);
        assertEquals("Error: unauthorized", messageResponse.message());
    }

    @Test
    @Order(9)
    public void listGameSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        LoginResult loginResponse = (LoginResult) ServerFacade.login("Joan", "joanPassword");
        CreateGameResult createGameResponse = (CreateGameResult) ServerFacade.createGame("game1", loginResponse.authToken());
        assertDoesNotThrow(() -> ServerFacade.listGame(loginResponse.authToken()));
    }

    @Test
    @Order(10)
    public void listGameFailed() throws IOException {
        MessageResult messageResponse = (MessageResult) ServerFacade.listGame(null);
        assertEquals("Error: unauthorized", messageResponse.message());
    }

    @Test
    @Order(11)
    public void logoutSuccess() throws IOException {
        RegisterResult registerResponse = (RegisterResult) ServerFacade.register("Joan", "joanPassword", "jjj@byu.edu");
        assertDoesNotThrow(() -> ServerFacade.logout(registerResponse.authToken()));
    }

    @Test
    @Order(12)
    public void logoutFailed() throws IOException {
        MessageResult messageResponse = (MessageResult) ServerFacade.logout(null);
        assertEquals("Error: unauthorized", messageResponse.message());
    }

}
