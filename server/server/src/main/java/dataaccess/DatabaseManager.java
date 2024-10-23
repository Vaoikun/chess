package dataaccess;

import java.sql.*;
import java.util.Properties; //The Properties class represents a persistent set of properties.

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String URL;

    //load database to db.properties file
    static {
        try{
            try (var prptyStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")){
                if (prptyStream == null) throw new Exception("db.properties not found");{
                    Properties properties = new Properties();
                    properties.load(prptyStream);
                    DATABASE_NAME = properties.getProperty("db.name");
                    USER = properties.getProperty("db.user");
                    PASSWORD = properties.getProperty("db.password");
                    var host = properties.getProperty("db.host");
                    var port = Integer.parseInt(properties.getProperty("db.port"));
                    URL = String.format("jdbc:mysql://%s:%d", host, port);
                }
            }
        } catch( Exception e ){
            throw new RuntimeException("Failed to load properties file" + e.getMessage());
        }
    }

    /**
     * create database if not exists
     */
    static public void createDatabase() throws DataAccessException{
        try{
            var creation_call = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var connct = DriverManager.getConnection(URL, USER, PASSWORD);
            try (var prepStatement = connct.prepareStatement(creation_call)) {
                prepStatement.executeUpdate();
            }
            }catch (SQLException e){
                throw new DataAccessException(e.getMessage());
        }
    }

    static Connection getConnection() throws DataAccessException{
        try{
            var connct = DriverManager.getConnection(URL, USER, PASSWORD);
            connct.setCatalog(DATABASE_NAME);
            return connct;
        } catch (SQLException e){
            throw new DataAccessException(e.getMessage());
        }
    }
}
