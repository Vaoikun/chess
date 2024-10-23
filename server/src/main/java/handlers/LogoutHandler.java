package handlers;

import httpresponse.MessageResponse;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler
{


    public LogoutHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String jsonResponse;

        // get the request authToken from header
        String authToken = request.headers("Authorization");
        try
        {
            final LogoutService logoutServiceRefer = new LogoutService();
            // call the register service
            logoutServiceRefer.logout(authToken);
            // setStatus
            jsonResponse = new Gson().toJson(new MessageResponse("")); //no message
            response.status(200);

        } catch (ServerException e)
        {
            response.status(500);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));

        } catch (DataAccessException e) {
           response.status(401);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        }
        response.type("application/json");
        return jsonResponse;
    }
}
