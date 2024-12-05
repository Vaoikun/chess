package websocket.commands.websocketrequest;

import websocket.commands.UserGameCommand;

public class Leave extends UserGameCommand {
    private Integer gameID;

    public Leave(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
