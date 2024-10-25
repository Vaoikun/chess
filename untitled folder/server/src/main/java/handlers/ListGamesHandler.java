package handlers;

import httpresponse.LIstGameResponse;
import httpresponse.MessageResponse;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler
{
    public ListGamesHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        String jsonResponse;
        String authToken = request.headers("Authorization");
        Gson gson = new Gson();
        try
        {
            final ListGamesService listGameServiceRefer = new ListGamesService();
            LIstGameResponse lIstGameResponse = listGameServiceRefer.listGame(authToken);
            response.status(200);
            jsonResponse = gson.toJson(lIstGameResponse);

        } catch (ServerException e) {
            response.status(500);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (DataAccessException e) {
            response.status(401);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        }
        return jsonResponse;
    }
}
