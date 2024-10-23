package handlers;

import httprequest.CreateGameRequest;
import httpresponse.CreateGameResponse;
import httpresponse.MessageResponse;
import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.CreateGameService;
import spark.Request;
import spark.Response;

public class CreateGameHandler extends BaseHandler {


    public CreateGameHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    /**
     * @param request
     * @param response
     * @return
     */

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String createdGameJsonReturn;
        try
        {
            CreateGameService createGameServiceRefer = new CreateGameService();
            // convert to createGameRequest class
            CreateGameRequest createGameRequest = getBody(request, CreateGameRequest.class);

            // get the authToken
            String authToken = request.headers("Authorization");
            CreateGameResponse createGameResponse = createGameServiceRefer.createGame(createGameRequest, authToken);
            int gameID = createGameResponse.gameID();
            createdGameJsonReturn = gson.toJson(createGameResponse);
            response.status(200);

        } catch (ServerException e)
        {
            response.status(500);
            createdGameJsonReturn = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (ClientException e)
        {
            response.status(400);
            createdGameJsonReturn = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (DataAccessException e)
        {
            response.status(401);
            createdGameJsonReturn = gson.toJson(new MessageResponse(e.getMessage()));
        }
        response.type("application/json");
        return createdGameJsonReturn;
    }
}
