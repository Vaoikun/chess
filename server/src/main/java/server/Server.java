package server;

import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.delete("/db", (request, response) -> new ClearHandler(request, response).httpHandlerRequest(request, response));
        Spark.post("/user", (request, response) -> new RegisterHandler(request, response).httpHandlerRequest(request, response));
        Spark.post("/session", (request, response) -> new LoginHandler(request, response).httpHandlerRequest(request, response));
        Spark.delete("/session", (request, response) -> new LogoutHandler(request, response).httpHandlerRequest(request, response));
        Spark.get("/game", (request, response) -> new ListGameHandler(request, response).httpHandlerRequest(request, response));
        Spark.post("/game", (request, response) -> new CreateGameHandler(request, response).httpHandlerRequest(request, response));
        Spark.put("/game", (request, response) -> new JoinGameHandler(request, response).httpHandlerRequest(request, response));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
