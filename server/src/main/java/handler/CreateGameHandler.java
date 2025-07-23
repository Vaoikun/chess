package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.CreateGameRequest;
import httpresponse.CreateGameResponse;
import httpresponse.MessageResponse;
import server.Server;
import server.ServerException;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends BaseHandler {
    public CreateGameHandler(Request request, Response response) throws DataAccessException, ServerException {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String json;
        try {
            GameService createGameService = new GameService();
            CreateGameRequest createGameRequest = getBody(request, CreateGameRequest.class);
            String authToken = request.headers("Authorization");
            CreateGameResponse createGameResponse = createGameService.createGame(createGameRequest, authToken);
            int gameID = createGameResponse.gameID();
            json = gson.toJson(createGameResponse);
            response.status(200);
        } catch (DataAccessException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(401);
        } catch (ServerException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(500);
        } catch (ClientException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(400);
        }
        response.type("application/json");
        return json;
    }
}
