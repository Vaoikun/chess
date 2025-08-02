package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import httprequest.CreateGameRequest;
import httprequest.JoinGameRequest;
import httprequest.LoginRequest;
import httprequest.RegisterRequest;
import httpresponse.*;

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
            try (InputStream inputStream = httpURLConnection.getErrorStream()) {
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

    public static Object login (String username, String password) throws IOException {
        LoginRequest request = new LoginRequest(username, password);
        String path = "/session";
        URL netURL = new URL(serverURL + path);
        String method = "POST";
        HttpURLConnection httpURLConnection = sendRequest(netURL, request, method, null);
        return getResponse(httpURLConnection, LoginResponse.class);
    }

    public static MessageResponse logout (String authToken) throws IOException {
        String path = "/session";
        URL netURL = new URL(serverURL + path);
        String method = "DELETE";
        HttpURLConnection httpURLConnection = sendRequest(netURL, null, method, null);
        return (MessageResponse) getResponse(httpURLConnection, MessageResponse.class);
    }

    public static Object createGame (String gameName, String authToken) throws IOException {
        CreateGameRequest request = new CreateGameRequest(gameName);
        String path = "/game";
        URL netURL = new URL(serverURL + path);
        String method = "POST";
        HttpURLConnection httpURLConnection = sendRequest(netURL, request, method, authToken);
        return getResponse(httpURLConnection, CreateGameResponse.class);
    }

    public static MessageResponse joinGame (int gameID, ChessGame.TeamColor teamColor, String authToken)
            throws IOException {
        JoinGameRequest request = new JoinGameRequest(teamColor, gameID);
        String path = "/game";
        URL netURL = new URL(serverURL + path);
        String method = "PUT";
        HttpURLConnection httpURLConnection = sendRequest(netURL, request, method, authToken);
        return (MessageResponse) getResponse(httpURLConnection, MessageResponse.class);
    }

    public static Object listGames (String authToken) throws IOException {
        String path = "/game";
        URL netURL = new URL(serverURL + path);
        String method = "GET";
        HttpURLConnection httpURLConnection = sendRequest(netURL, null, method, authToken);
        return getResponse(httpURLConnection, ListGameResponse.class);
    }

    public static MessageResponse clear() throws IOException {
        String path = "/db";
        URL netURL = new URL(serverURL + path);
        String method = "DELETE";
        HttpURLConnection httpURL = sendRequest(netURL, null, method, null);
        return (MessageResponse) getResponse(httpURL, MessageResponse.class);
    }

}
