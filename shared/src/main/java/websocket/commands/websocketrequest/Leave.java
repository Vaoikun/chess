package websocket.commands.websocketrequest;

import websocket.commands.UserGameCommand;

public class Leave extends UserGameCommand {

    public Leave(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
