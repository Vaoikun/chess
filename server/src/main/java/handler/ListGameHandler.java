package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.JoinGameRequest;
import httpresponse.MessageResponse;
import server.ServerException;
import service.FullGameException;
import service.GameService;
import spark.Request;
import spark.Response;

public class ListGameHandler extends BaseHandler{
    public ListGameHandler(Request request, Response response) {
        super (request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String json;
        try {
            GameService joinGameService = new GameService();
            String authToken = request.headers("Authorization");
            JoinGameRequest joinGameRequest = getBody(request, JoinGameRequest.class);
            joinGameService.joinGame(joinGameRequest, authToken);
            json = new Gson().toJson(new MessageResponse(""));
            response.status(200);
        } catch (ClientException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(400);
        } catch (DataAccessException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(401);
        } catch (ServerException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(500);
        } catch (FullGameException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(403);
        }
        response.type("application/json");
        return json;
    }
}
