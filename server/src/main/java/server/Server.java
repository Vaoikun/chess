package server;

import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.post("/user", (request, response) -> new RegisterHandler(request, response).httpHandlerRequest(request, response));
        Spark.post("/session", (reqest, response) -> new LoginHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/session", (reqest, response) -> new LogoutHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.get("/game", (reqest, response) -> new ListGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.post("/game", (reqest, response) -> new CreateGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.put("/game", (reqest, response) -> new JoinGameHandler(reqest, response).httpHandlerRequest(reqest, response));
        Spark.delete("/db", (request, response) -> new ClearHandler(request, response).httpHandlerRequest(request, response));
        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
