package server;

import dataaccess.DatabaseManager;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import spark.*;
import dataaccess.DataAccessException;
import handler.*;
import static dataaccess.DatabaseManager.createDatabase;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", WebSocketHandler.class);
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (reqest, response) -> new RegisterHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.post("/session", (reqest, response) -> new LoginHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/session", (reqest, response) -> new LogoutHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.get("/game", (reqest, response) -> new ListGamesHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.post("/game", (reqest, response) -> new CreateGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.put("/game", (reqest, response) -> new JoinGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/db", (reqest, response) -> new ClearHandler(reqest, response).httpHandlerRequest(reqest, response));

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();

        try{
            DatabaseManager.createDatabase();
        }catch (DataAccessException e){
            throw new RuntimeException(e);
        }
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
