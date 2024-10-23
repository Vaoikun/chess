package handlers;

import httpresponse.MessageResponse;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler
{

    public ClearHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    /**
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object httpHandlerRequest(Request request, Response response){
        Gson gson = new Gson();
        String body;
        try
        {
            ClearService clearServiceRefer = new ClearService();
            clearServiceRefer.clear();
            body = new Gson().toJson(new MessageResponse("")); //no message
            response.status(200);

        }
        catch(DataAccessException e)
        {
            body = gson.toJson(new MessageResponse(e.getMessage()));
        }
        catch (ServerException e)
        {
            body = new Gson().toJson(new MessageResponse(e.getMessage()));
            response.status(500);
        }
        response.type("application/type");
        return body;
    }
}
