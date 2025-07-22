package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import httpresponse.MessageResponse;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {

    public ClearHandler (Request request, Response response) {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) throws ServerException, DataAccessException {
        Gson gson = new Gson();
        String body;
        try {
            UserService clearService = new UserService();
            clearService.clear();
            body = new Gson().toJson(new MessageResponse(""));
            response.status(200);
        } catch (DataAccessException e) {
            body = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (ServerException e) {
            body = new Gson().toJson(new MessageResponse(e.getMessage()));
            response.status(200);
        }
        response.type("application/type");
        return body;
    }

}
