package websocket.commands.websocketrequest;

import websocket.commands.UserGameCommand;

public class Resign extends UserGameCommand {
    private Integer gameID;

    public Resign(String authToken, Integer gameID) {
        super(CommandType.RESIGN, authToken, gameID);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
