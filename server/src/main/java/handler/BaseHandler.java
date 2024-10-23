package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.ServerException;

public abstract class BaseHandler {
    private spark.Request request;
    private spark.Response response;
    BaseHandler(spark.Request request, spark.Response response) {
        this.request = request;
        this.response = response;
    }

    public abstract Object httpHandlerRequest(spark.Request request, spark.Response response) throws ServerException, DataAccessException;

    protected static <T> T getBody (spark.Request request, Class<T> aClass) {
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
