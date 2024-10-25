package handler;

import httpresult.MessageResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import org.eclipse.jetty.http.HttpParser;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {
    public ClearHandler(Request request, Response response) throws DataAccessException {
        super(request, response);
    }

    /**
     * @param request;
     * @param response;
     * @return String body
     */
    @Override
    public Object httpHandlerRequest(Request request, Response response) {
        Gson gson = new Gson();
        String body;
        try{
            ClearService clearService = new ClearService();
            clearService.clear();
            body = new Gson().toJson(new MessageResult(""));
            response.status(200);
        }catch (DataAccessException e){
            body = gson.toJson(new MessageResult(e.getMessage()));
        }catch (ServerException e){
            body = new Gson().toJson(new MessageResult(e.getMessage()));
            response.status(500);
        }
        response.type("application/type"); // could be type json
        return body;
    }
}
