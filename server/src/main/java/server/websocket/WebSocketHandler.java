package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;



@WebSocket
public class WebSocketHandler {
    private static final ConnectionManager CONNECTION_MANAGER = new ConnectionManager();

    public enum KEYITEMS {join, observe, move, leave, resign, check, checkmate}

    @OnWebSocketMessage
    public void onMessage(Session session, String message)
    {
        Gson gson = new Gson();
        UserGameCommand userGameCommand = gson.fromJson(message, UserGameCommand.class);

        switch (userGameCommand.getCommandType())
        {
            case UserGameCommand.CommandType.CONNECT -> observeOrJoin(message, session);
            case UserGameCommand.CommandType.LEAVE -> leave(message, session);
            case UserGameCommand.CommandType.MAKE_MOVE -> movePiece(message, session);
            case UserGameCommand.CommandType.RESIGN -> resign(message, session);
            // how about the check and checkmate?
        }
    }

    public static void observeOrJoin{};

    public static void leave{return;}

    public static void movePiece{return leave;}

    public static void resign{return;}
}
