package handler;

import httpresult.ListGameResult;
import httpresult.MessageResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.ListGamesService;
import spark.Request;
import spark.Response;

public class ListGamesHandler extends BaseHandler{
    public ListGamesHandler(Request request, Response response) throws DataAccessException, ServerException {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        String jsonResult;
        String authToken = request.headers("Authorization");
        Gson gson = new Gson();
        try{
            final ListGamesService listGameService = new ListGamesService();
            ListGameResult listGameResult = listGameService.listGame(authToken);
            response.status(200);
            jsonResult = gson.toJson(listGameResult);
        }catch (ServerException e){
            response.status(500);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }catch (DataAccessException e){
            response.status(401);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }
        return jsonResult;
    }
}
