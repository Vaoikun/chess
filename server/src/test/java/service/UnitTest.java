package service;

import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresponse.LoginResponse;
import httpresponse.RegisterResponse;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.ServerException;
import static org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.Data;

public class UnitTest {
    private String authToken;
    ///test2
    private final UserService registerService1 = new UserService();
    private final RegisterRequest registerRequest1 = new RegisterRequest("Mole", "rat", "molerat@email.com");
    private final UserService loginService1 = new UserService();
    private final UserService logoutService1 = new UserService();

    public UnitTest() throws DataAccessException {
    }

    @Test
    @Order(1)
    public void clear() throws ServerException, DataAccessException {
        UserService clearServiceTest = new UserService();
        assertDoesNotThrow(clearServiceTest::clear);
    }

    @Test
    @Order(2)
    public void registerSuccess() throws ServerException, DataAccessException, ClientException {
        RegisterResponse registerResponse = registerService1.register(registerRequest1);
        String username = registerResponse.username();
        assertEquals(registerResponse.username(), username);
        authToken = registerResponse.authToken();
    }

    @Test
    @Order(3)
    public void registerFailed() throws ServerException, DataAccessException, ClientException {
        RegisterRequest missingPassword = new RegisterRequest("Mike", null, "mike@email.com");
        ClientException exception = assertThrows(ClientException.class, () -> registerService1.register(missingPassword));
        assertEquals(exception.getMessage(), "must set the password.");
    }

    @Test
    @Order(4)
    public void loginSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterResponse registerResponse = registerService1.register(new RegisterRequest("King", "Gnik", "king@email.com"));
        LoginResponse loginResponse = loginService1.login(new LoginRequest("King", "Gnik"));
        String username = registerResponse.username();
        assertEquals(username, loginResponse.username());
    }

    @Test
    @Order(5)
    public void loginFailed() throws ServerException, ClientException, DataAccessException {
        LoginRequest loginRequest = new LoginRequest("King", null);
        DataAccessException exception = assertThrows(DataAccessException.class, () -> loginService1.login(loginRequest));
        assertEquals(exception.getMessage(), "unauthorized.");
    }

    @Test
    @Order(6)
    public void logoutSuccess() throws ServerException, ClientException, DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Queen", "Neeuq", "queen@email.com");
        RegisterResponse registerResponse = registerService1.register(registerRequest);
        String authToken = registerResponse.authToken();
        LoginResponse loginResponse = loginService1.login(new LoginRequest("Queen", "Neeuq"));
        assertDoesNotThrow(() -> logoutService1.logout(authToken));
    }

    @Test
    @Order(7)
    public void logoutFailed() throws ServerException, ClientException, DataAccessException {
        DataAccessException exception = assertThrows(DataAccessException.class, () -> logoutService1.logout(null));
        assertEquals(exception.getMessage(), "unauthorized.");
    }


}
