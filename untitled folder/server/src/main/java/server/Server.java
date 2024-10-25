package server;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import handlers.*;
import spark.*;


import static dataaccess.DatabaseManager.createDatabase;

public class Server {
    public int run(int desiredPort){
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", WebsocketHandler.class); // we are going to create a websocket handler to handle it?
        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> new RegisterHandler(req, res).httpHandlerRequest(req, res));
        Spark.post("/session", (req, res) -> new LoginHandler(req, res).httpHandlerRequest(req, res));
        Spark.delete("/session", (req, res) -> new LogoutHandler(req, res).httpHandlerRequest(req, res));
        Spark.get("/game", (req, res) -> new ListGamesHandler(req, res).httpHandlerRequest(req, res));
        Spark.post("/game", (req, res) -> new CreateGameHandler(req, res).httpHandlerRequest(req, res));
        Spark.put("/game", (req, res) -> new JoinGameHandler(req, res).httpHandlerRequest(req, res));
        Spark.delete("/db", (req, res) -> new ClearHandler(req, res).httpHandlerRequest(req, res));
        Spark.awaitInitialization();

        try
        {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
