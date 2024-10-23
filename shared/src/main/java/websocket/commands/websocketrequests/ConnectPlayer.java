package websocket.commands.websocketrequests;

import websocket.commands.UserGameCommand;

public class ConnectPlayer extends UserGameCommand {

    private int gameID;

    public ConnectPlayer(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.CONNECT;
    }

    public int getGameID() {
        return gameID;
    }
}
