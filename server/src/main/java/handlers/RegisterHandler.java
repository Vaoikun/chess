package handlers;

import httprequest.RegisterRequest;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;
import com.google.gson.Gson;
import dataaccess.ClientException;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.RegisterService;
import spark.Request;
import spark.Response;

public class RegisterHandler extends BaseHandler
{

    public RegisterHandler(Request request, spark.Response response) throws DataAccessException {
        super(request, response);
    }

    @Override
    public  Object httpHandlerRequest(Request request, Response response)
    {
        Gson gson = new Gson();
        String jsonResponse;
        try
        {
            final RegisterService registerServiceRefer = new RegisterService();
            // make the json request to be java request objects
            RegisterRequest body = getBody(request, RegisterRequest.class); // change the request to java object
            // body.password();
            // call the register service
            RegisterResponse registerResponse =  registerServiceRefer.register(body);

            // setStatus
            response.status(200);

            // set to be json
            jsonResponse = gson.toJson(registerResponse);
        }
        catch (DataAccessException ex)
        {
            response.status(403); // already taken

            //  catch the error message of DE and put it into a new response and make it a json
            jsonResponse = gson.toJson(new MessageResponse(ex.getMessage()));
        }
        catch (ClientException e)
        {
            response.status(400); // bad request

            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        }
        catch (ServerException e)
        {
            response.status(500);

            jsonResponse = gson.toJson(new MessageResponse(e.getMessage()));
        }

        response.type("application/json");
        return jsonResponse;
    }
}
