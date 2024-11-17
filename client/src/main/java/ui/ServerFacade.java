package ui;

import com.google.gson.Gson;
import chess.ChessGame;
import httprequest.*;
import httpresult.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private static String httpURL;

    public ServerFacade(String httpURL) {
        ServerFacade.httpURL = httpURL;
    }

    public static Object register(String username, String password, String email) throws IOException {
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        String path = "/user";
        URL url = new URL(httpURL + path);
        String method = "POST";
        HttpURLConnection http = sendRequest(url, registerRequest, method, null);

        return getResultFromHandlers(http, RegisterResult.class);
    }

    public static Object login(String username, String password) throws IOException {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String path = "/session";
        URL url = new URL(httpURL + path);
        String method = "POST";
        HttpURLConnection http = sendRequest(url, loginRequest, method, null);

        return getResultFromHandlers(http, LoginResult.class);
    }

    public static MessageResult logout(String authToken) throws IOException {
        String path = "/session";
        URL url = new URL(httpURL + path);
        String method = "DELETE";
        HttpURLConnection http = sendRequest(url, null, method, authToken);

        return (MessageResult) getResultFromHandlers(http, LoginResult.class);
    }

    public static MessageResult clear() throws IOException {
        String path = "/db";
        URL uri = new URL(httpURL + path);
        String method = "DELETE";
        HttpURLConnection http = sendRequest(uri, null, method, null);
        return (MessageResult) getResultFromHandlers(http, MessageResult.class);
    }

    public static Object createGame(String gameName, String authToken) throws IOException {
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        String path = "/game";
        URL url = new URL(httpURL + path);
        String method = "POST";
        HttpURLConnection http = sendRequest(url, createGameRequest, method, authToken);

        return getResultFromHandlers(http, CreateGameResult.class);
    }

    public static Object listGame(String authToken) throws IOException {
        String path = "/game";
        URL url = new URL(httpURL + path);
        String method = "GET";
        HttpURLConnection http = sendRequest(url, null, method, authToken);

        return getResultFromHandlers(http, ListGameResult.class);
    }

    public static MessageResult joinGame(ChessGame.TeamColor playerColor, int gameID, String authToken) throws IOException {
        JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
        String path = "/game";
        URL url = new URL(httpURL + path);
        String method = "PUT";
        HttpURLConnection http = sendRequest(url, joinGameRequest, method, authToken);

        return (MessageResult) getResultFromHandlers(http, MessageResult.class);
    }

    private static HttpURLConnection sendRequest(URL uri, Object requestBody, String method, String authToken) throws IOException {
        Gson gson = new Gson();
        HttpURLConnection http = (HttpURLConnection) uri.openConnection();
        http.setRequestMethod(method);
        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
        if (requestBody != null) {
            String jsonRequestBody = gson.toJson(requestBody); // make it as json before putting into body
            writeRequestBody(jsonRequestBody, http);
        }
        http.connect();
        return http; // return the http that already had the request
    }

    private static void writeRequestBody(String jsonRequestBody, HttpURLConnection http) throws IOException {
        // I think I also need to set the authToken into Body right? But authToken should be set in header.
        if (!jsonRequestBody.isEmpty())
        {
//            http.addRequestProperty("content");
            http.setDoOutput(true); // I can write request into body
            try (var outputStream = http.getOutputStream())
            {
                outputStream.write(jsonRequestBody.getBytes()); // put it into body
            }
        }
    }

    private static Object getResultFromHandlers(HttpURLConnection http, Type type) throws IOException
    {
        Object responseBody;
        Gson gson = new Gson();
        if (http.getResponseCode() == 200)
        {
            try (InputStream respBody = http.getInputStream())
            {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = gson.fromJson(inputStreamReader, type);
            }
        }
        else
        {
            try(InputStream respBody = http.getErrorStream())
            {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                responseBody = gson.fromJson(inputStreamReader, MessageResult.class);
            }

        }
        return responseBody;
    }
}
