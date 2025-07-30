package ui;

import chess.ChessGame;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.ERASE_SCREEN;

public class BoardUI {
    private static final int COLUMNS = 8;
    private static final int ROW = 8;
    public static ChessGame.TeamColor teamColor;

    public static void main (String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
    }



}
