package service;

import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.RegisterRequest;
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



}
