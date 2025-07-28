package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.JoinGameRequest;
import httpresponse.ListGameResponse;
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
            final GameService listGameService = new GameService();
            String authToken = request.headers("Authorization");
            ListGameResponse listGameResponse = listGameService.listGames(authToken);
            json = gson.toJson(listGameResponse);
            response.status(200);
        } catch (DataAccessException | ClientException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(401);
        } catch (ServerException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(500);
        } catch (FullGameException e) {
            json = gson.toJson(new MessageResponse(e.getMessage()));
            response.status(403);
        }
        return json;
    }
}
