package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import httprequest.JoinGameRequest;
import dataaccess.AlreadyTakenException;
import httpresult.MessageResult;
import service.JoinGameService;
import spark.Response;
import spark.Request;

public class JoinGameHandler extends BaseHandler {
    public JoinGameHandler(Request request, Response response) {
        super(request, response);
    }

    /**
     * @param request;
     * @param response;
     * @return jsonResult
     */
    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String jsonResult;
        try {
            JoinGameService joinGameService = new JoinGameService();
            String authToken = request.headers("Authorization");
            JoinGameRequest joinGameRequest = getBody(request, JoinGameRequest.class);
            joinGameService.joinGame(joinGameRequest, authToken);
            jsonResult = new Gson().toJson(new MessageResult("")); //no message
            response.status(200);

        } catch (ServerException e) {
            response.status(500);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (ClientException e) {
            response.status(400);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (DataAccessException e) {
            response.status(401);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (AlreadyTakenException e)
        {
            response.status(403);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }
        response.type("application/json");
        return jsonResult;
    }
}
