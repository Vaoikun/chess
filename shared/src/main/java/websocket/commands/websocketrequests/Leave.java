package websocket.commands.websocketrequests;

import websocket.commands.UserGameCommand;

public class Leave extends UserGameCommand {
    private int gameID;
    public Leave(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }

}
