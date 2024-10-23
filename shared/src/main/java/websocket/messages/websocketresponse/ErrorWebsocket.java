package websocket.messages.websocketresponse;

import websocket.messages.ServerMessage;

public class ErrorWebsocket extends ServerMessage {

    private String errorMessage;

    public ErrorWebsocket(ServerMessageType type) {
        super(type);
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }



}
