package model;

import chess.ChessGame;

public record GameData(int gameID, String gameName, String whiteUsername, String blackUsername, ChessGame game){

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
