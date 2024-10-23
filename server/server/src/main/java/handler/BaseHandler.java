package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;
import spark.Request;
import spark.Response;

public abstract class BaseHandler {
    private Request request;
    private Response response;
    BaseHandler(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public abstract Object httpHandlerRequest(Request request, Response response) throws ServerException, DataAccessException;

    protected static <T> T getBody (Request request, Class<T> aClass) {
        Gson gson = new Gson();
        String requestJson = request.body();
        T tbody = gson.fromJson(requestJson, aClass); // convert requestJson to generic class type
        if (tbody == null) {
            throw new RuntimeException("No request body found");
        }else{
            return tbody;
        }
    }
}
