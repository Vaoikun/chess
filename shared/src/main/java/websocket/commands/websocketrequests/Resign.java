package websocket.commands.websocketrequests;

import websocket.commands.UserGameCommand;

public class Resign extends UserGameCommand {

    private int gameID;
    public Resign(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID() {
        return gameID;
    }
}
