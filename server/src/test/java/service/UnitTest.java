package service;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import server.ServerException;
import static org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.Data;

public class UnitTest {
    private String authToken;

    public UnitTest() throws DataAccessException {
    }

    @Test
    @Order(1)
    public void clear() throws ServerException, DataAccessException {
        UserService clearServiceTest = new UserService();
        assertDoesNotThrow(clearServiceTest::clear);
    }

}
