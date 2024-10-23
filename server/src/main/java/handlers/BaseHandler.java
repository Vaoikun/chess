package handlers;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.ServerException;
import org.eclipse.jetty.server.Response;
import spark.Request;

public abstract class BaseHandler {
    // if the web API requires an authToken, write the logic to valid the authToken in base handler class

    private spark.Response response;
    private spark.Request request;
    BaseHandler(Request request, spark.Response response)
    {
        // use spark to create the request and response classes.
        this.response = response;
        this.request = request;
    }

    public abstract Object httpHandlerRequest(Request request, spark.Response response) throws ServerException, DataAccessException;

    protected static <T> T getBody (Request request, Class<T> aClass)
    {
        Gson gson = new Gson();
        // get the request parameters as json
        String requestJ = request.body();
       T tBody =  gson.fromJson(requestJ, aClass); // transfer the requestBody to be the Generic class type
        if (tBody != null)
        {
            return tBody;
        }
        else
        {
            throw new RuntimeException("You do not have request body");
        }

    }






}
