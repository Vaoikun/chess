package handler;

import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import httprequest.LoginRequest;
import httpresponse.LoginResponse;
import httpresponse.MessageResponse;
import org.eclipse.jetty.security.LoginService;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    public LoginHandler(Request request, Response response) throws DataAccessException, ServerException {
        super(request, response);
    }
    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String json;
        try {
            final UserService loginService = new UserService();
            LoginRequest body = getBody(request, LoginRequest.class);
            LoginResponse loginResponse = loginService.login(body);
            response.status(200);
            json = gson.toJson(loginResponse);
        } catch (ServerException e) {
            response.status(500);
            json = gson.toJson(new MessageResponse(e.getMessage()));
        } catch (DataAccessException e) {
            response.status(401);
            json = gson.toJson(new MessageResponse(e.getMessage()));
        }catch (ClientException e) {
            response.status(400);
            json = gson.toJson(new MessageResponse(e.getMessage()));
        }
        response.type("application/json");
        return json;
    }

}
