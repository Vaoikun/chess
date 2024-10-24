package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game){

    public boolean availableColor (String requestedColor) throws IllegalAccessException {
        if (requestedColor.equalsIgnoreCase("WHITE")) {
            return (this.whiteUsername == null);
        } else if (requestedColor.equalsIgnoreCase("BLACK"))
        {
            return (this.blackUsername == null);
        }
        else
        {
            throw new IllegalAccessException("Error: invalid parameter");
        }
    }
}
