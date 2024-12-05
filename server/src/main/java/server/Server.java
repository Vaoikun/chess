package server;


import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import spark.*;
import handler.*;
import server.websocket.WebSocketHandler;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/ws", WebSocketHandler.class);
        Spark.post("/user", (reqest, response) -> new RegisterHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.post("/session", (reqest, response) -> new LoginHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/session", (reqest, response) -> new LogoutHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.get("/game", (reqest, response) -> new ListGamesHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.post("/game", (reqest, response) -> new CreateGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.put("/game", (reqest, response) -> new JoinGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/db", (reqest, response) -> new ClearHandler(reqest, response).httpHandlerRequest(reqest, response));

        //This line initializes the server and can be removed once you have a functioning endpoint
        //Spark.init();

        try{
            DatabaseManager.createDatabase();
        } catch (DataAccessException e){
            throw new RuntimeException(e);
        }

        Spark.awaitInitialization();

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
