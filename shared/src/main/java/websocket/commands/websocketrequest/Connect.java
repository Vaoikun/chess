package websocket.commands.websocketrequest;

import com.google.gson.annotations.SerializedName;
import websocket.commands.UserGameCommand;

public class Connect extends UserGameCommand {

    public Connect(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
