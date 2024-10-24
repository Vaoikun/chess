package handler;

import httpresult.MessageResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler extends BaseHandler{
    public LogoutHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String jsonResult;
        String authToken = request.headers("Authorization");
        try{
            final LogoutService logoutService = new LogoutService();
            logoutService.logout(authToken);
            jsonResult = new Gson().toJson(new MessageResult(""));
            response.status(200);
        } catch (ServerException e) {
            response.status(500);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (DataAccessException e) {
            response.status(401);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }
        response.type("application/json");
        return jsonResult;
    }
}
