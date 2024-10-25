package handler;

import httprequest.LoginRequest;
import httpresult.LoginResult;
import httpresult.MessageResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler extends BaseHandler {
    public LoginHandler(Request request, Response response)throws DataAccessException, ServerException {
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
            final LoginService loginService = new LoginService();
            LoginRequest loginBody =  getBody(request, LoginRequest.class);
            LoginResult loginResponse = loginService.login(loginBody);
            response.status(200);
            jsonResult = gson.toJson(loginResponse);
        }catch (ServerException e) {
            response.status(500);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }catch (DataAccessException e) {
            response.status(401);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }
        response.type("application/json");
        return jsonResult;
    }
}
