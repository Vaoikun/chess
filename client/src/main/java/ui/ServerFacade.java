package ui;

import com.google.gson.Gson;
import httprequest.RegisterRequest;
import httpresponse.MessageResponse;
import httpresponse.RegisterResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private static String serverURL;

    public ServerFacade(String serverURL) {
        ServerFacade.serverURL = serverURL;
    }

    private static HttpURLConnection sendRequest (URL httpURL, Object requestBody, String method, String authToken)
            throws IOException {
        Gson json = new Gson();
        HttpURLConnection httpURLConnection = (HttpURLConnection) httpURL.openConnection();
        httpURLConnection.setRequestMethod(method);
        if (authToken != null) {
            httpURLConnection.addRequestProperty("authorization", authToken);
        }
        if (requestBody != null) {
            String jsonRequestBody = json.toJson(requestBody);
            writeRequestBody(jsonRequestBody, httpURLConnection);
        }
        httpURLConnection.connect();
        return httpURLConnection;
    }

    private static void writeRequestBody (String jsonRequestBody, HttpURLConnection httpURLConnection) throws IOException {
        if (!jsonRequestBody.isEmpty()) {
            httpURLConnection.setDoOutput(true);
            try (var outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(jsonRequestBody.getBytes());
            }
        }
    }

    private static Object getResponse (HttpURLConnection httpURLConnection, Type type) throws IOException {
        Object responseBody;
        Gson json = new Gson();
        if (httpURLConnection.getResponseCode() == 200) {
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                InputStreamReader input = new InputStreamReader(inputStream);
                responseBody = json.fromJson(input, type);
            }
        } else {
            try (InputStream inputStream = httpURLConnection.getInputStream()) {
                InputStreamReader input = new InputStreamReader(inputStream);
                responseBody = json.fromJson(input, MessageResponse.class);
            }
        }
        return responseBody;
    }

    public static Object register (String username, String password, String email) throws IOException {
        RegisterRequest request = new RegisterRequest(username, password, email);
        String path = "/user";
        URL netURL = new URL(serverURL + path);
        String method = "POST";
        HttpURLConnection httpURLConnection = sendRequest(netURL, request, method, null);
        return getResponse(httpURLConnection, RegisterResponse.class);
    }

}
