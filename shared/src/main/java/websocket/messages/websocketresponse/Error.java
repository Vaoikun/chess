package websocket.messages.websocketresponse;

import websocket.messages.ServerMessage;

public class Error extends ServerMessage {
    private String message;
    public Error(ServerMessageType type) {
        super(type);
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
