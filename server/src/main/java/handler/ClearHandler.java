package handler;

import dataaccess.DataAccessException;
import server.ServerException;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {

    public ClearHandler (Request request, Response response) {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) throws ServerException, DataAccessException {
        return null;
    }

}
