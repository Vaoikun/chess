package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import server.ServerException;
import service.UserService;
import spark.Request;
import spark.Response;

public class ClearHandler extends BaseHandler {

    public ClearHandler (Request request, Response response) {
        super(request, response);
    }

    @Override
    public Object httpHandlerRequest(Request request, Response response) throws ServerException, DataAccessException {
        Gson gson = new Gson();
        String body;
        try {
            UserService clearService = new UserService();
        }
    }

}
