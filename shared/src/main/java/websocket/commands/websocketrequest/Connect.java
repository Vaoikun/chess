package websocket.commands.websocketrequest;

import websocket.commands.UserGameCommand;

public class Connect extends UserGameCommand {
    private Integer gameID;
    private CommandType commandType;

    public Connect(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }

    public Integer getGameID(){
        return gameID;
    }
}
