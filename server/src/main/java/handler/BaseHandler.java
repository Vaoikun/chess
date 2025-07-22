package handler;

import dataaccess.DataAccessException;
import server.ServerException;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;


public abstract class BaseHandler {
    private Request request;
    private Response response;

    public abstract Object httpHandlerRequest(Request request, Response response)
            throws ServerException, DataAccessException;

    BaseHandler(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    protected static <T> T getBody (Request request, Class<T> aClass) {
        Gson gson = new Gson();
        String json = request.body();
        T body = gson.fromJson(json, aClass);
        if (body == null) {
            throw new RuntimeException("No request body found.");
        }else{
            return body;
        }
    }

}
