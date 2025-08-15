package websocket.messages.websocketresponse;

import websocket.messages.ServerMessage;

public class Error extends ServerMessage {
    private String errorMessage;
    public Error(ServerMessageType type) {
        super(type);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
