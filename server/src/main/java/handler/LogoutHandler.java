package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.SQLUserDAO;
import httprequest.LoginRequest;
import httpresponse.LoginResponse;
import httpresponse.MessageResponse;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class LogoutHandler extends BaseHandler{
    public LogoutHandler(Request request, Response response) throws DataAccessException, ServerException {
        super(request, response);
    }
    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String json;
        String authToken = request.headers("Authorization");
        try {
            final UserService logoutService = new UserService();
            logoutService.logout(authToken);
            response.status(200);
            json = new Gson().toJson(new MessageResponse(""));
        } catch (ServerException e) {
            response.status(500);
            json = gson.toJson(new MessageResponse("Error: " + e.getMessage()));
        } catch (DataAccessException | SQLException e) {
            response.status(401);
            json = gson.toJson(new MessageResponse(e.getMessage()));
        }
        response.type("application/json");
        return json;
    }
}
