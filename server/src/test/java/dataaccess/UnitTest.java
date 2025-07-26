package dataaccess;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UnitTest {
    private final SQLUserDAO SQLUserDB = new SQLUserDAO();
    private final SQLGameDAO SQLGameDB = new SQLGameDAO();
    private final SQLAuthDAO SQLAuthDB = new SQLAuthDAO();

    public UnitTest() throws DataAccessException {
    }

    @BeforeEach
    public void createDB() throws DataAccessException {
        DatabaseManager.createDatabase();
        SQLUserDAO.createUserTable();
        SQLAuthDAO.createAuthTable();
    }

    @AfterEach
    public void clear() throws DataAccessException {
        SQLUserDB.clear();
        SQLAuthDB.clear();
    }

    @Test
    @Order(1)
    public void clearTest() throws DataAccessException {
        SQLUserDB.clear();
        SQLAuthDB.clear();
        assertDoesNotThrow(SQLUserDB::clear);
        assertDoesNotThrow(SQLAuthDB::clear);
    }
}
