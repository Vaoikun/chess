package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.RegisterRequest;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;

import java.sql.SQLException;

public class RegisterHandler extends BaseHandler{
    public RegisterHandler(Request request, Response response) {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String jsonResponse;
        try{
            final UserService registerService = new UserService();
            RegisterRequest body = getBody(request, RegisterRequest.class);
            RegisterResponse registerResponse = registerService.register(body);
            response.status(200);
            jsonResponse = gson.toJson(registerResponse);
        } catch (DataAccessException | SQLException e){
            response.status(403);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (ClientException e) {
            response.status(400);
            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (ServerException e) {
            response.status(500);
            jsonResponse = gson.toJson(new MessageResponse("Error: " + e.getMessage()));
        }
        response.type("application/json");
        return jsonResponse;
    }
}
