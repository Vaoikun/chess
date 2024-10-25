package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import httprequest.CreateGameRequest;
import httpresult.CreateGameResult;
import httpresult.MessageResult;
import service.CreateGameService;
import spark.Response;
import spark.Request;

public class CreateGameHandler extends BaseHandler {
    public CreateGameHandler(Request request, Response response) throws DataAccessException, ServerException {
        super(request, response);
    }

    /**
     * @param request;
     * @param response;
     * @return createdJsonReturn
     */
    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String createdJasonReturn;
        try{
            CreateGameService createdGameService = new CreateGameService();
            CreateGameRequest createGameRequest = getBody(request, CreateGameRequest.class); //convert to createGameRequest class
            String authToken = request.headers("Authorization"); //get authToken
            CreateGameResult createGameResult = createdGameService.createGame(createGameRequest, authToken);
            int gameID = createGameResult.gameID();
            createdJasonReturn = gson.toJson(createGameResult);
            response.status(200);
        }catch(DataAccessException e){
            response.status(401);
            createdJasonReturn = gson.toJson(new MessageResult(e.getMessage()));
        }catch (ServerException e){
            response.status(500);
            createdJasonReturn = gson.toJson(new MessageResult(e.getMessage()));
        }catch(ClientException e){
            response.status(400);
            createdJasonReturn = gson.toJson(new MessageResult(e.getMessage()));
        }
        response.type("application/json");
        return createdJasonReturn;
    }
}
