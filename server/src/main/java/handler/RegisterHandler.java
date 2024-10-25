package handler;

import httprequest.RegisterRequest;
import httpresult.MessageResult;
import httpresult.RegisterResult;
import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.RegisterService;
import spark.Request;
import spark.Response;


public class RegisterHandler extends BaseHandler{
    public RegisterHandler(Request request, Response response)throws DataAccessException{
        super(request, response);
    }

    /**
     * @param request;
     * @param response;
     * @return jsonResult
     */
    @Override
    public  Object httpHandlerRequest(Request request, Response response)
    {
        Gson gson = new Gson();
        String jsonResult;
        try {
            final RegisterService registerService = new RegisterService();
            RegisterRequest body = getBody(request, RegisterRequest.class);
            RegisterResult registerResponse =  registerService.register(body);
            response.status(200);
            jsonResult = gson.toJson(registerResponse);
        } catch (DataAccessException e) {
            response.status(403);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (ClientException e) {
            response.status(400);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        } catch (ServerException e) {
            response.status(500);
            jsonResult = gson.toJson(new MessageResult(e.getMessage()));
        }
        response.type("application/json");
        return jsonResult;
    }
}
