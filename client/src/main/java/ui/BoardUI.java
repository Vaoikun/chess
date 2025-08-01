package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class BoardUI {
    private static final int COLUMNS = 8;
    private static final int ROWS = 8;
    private static final String[] blackTeamHeader = {"h", "g", "f", "e", "d", "c", "b", "a"};
    private static final String[] whiteTeamHeader = {"a", "b", "c", "d", "e", "f", "g", "h"};
    public static ChessGame.TeamColor teamColor;

    public static void main (String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
    }

    private static void grayTile (PrintStream OUT) {
        OUT.print(SET_BG_COLOR_LIGHT_GREY);
        OUT.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
    private static void whiteTile (PrintStream OUT) {
        OUT.print(SET_BG_COLOR_WHITE);
        OUT.print(SET_TEXT_COLOR_WHITE);
    }
    private static void blackTile (PrintStream OUT) {
        OUT.print(SET_BG_COLOR_BLACK);
        OUT.print(SET_TEXT_COLOR_BLACK);
    }
    private static void blank (PrintStream OUT) {
        OUT.print(RESET_TEXT_COLOR);
        OUT.print(RESET_BG_COLOR);
    }

    public static void callWhiteTiles (PrintStream OUT, ChessBoard board, Collection<ChessMove> legalMoves) {
        teamColor = WHITE;
        int startRow = 1;
        grayTile(OUT);
        OUT.print(" ");
        outputHeaders(OUT, whiteTeamHeader);
        drawBoard(OUT, startRow, board, legalMoves);
    }

    public static void callBlackTiles (PrintStream OUT, ChessBoard board, Collection<ChessMove> legalMoves) {
        teamColor = BLACK;
        int startRow = 1;
        grayTile(OUT);
        OUT.print(" ");
        outputHeaders(OUT, blackTeamHeader);
        drawBoard(OUT, startRow, board, legalMoves);
    }

    private static void outputHeaders (PrintStream OUT, String[] teamHeader) {
        grayTile(OUT);
        OUT.print(" ");
        for (int col =0; col < COLUMNS; col++) {
            OUT.print("/u2003 ");
            OUT.print(SET_TEXT_COLOR_BLACK);
            OUT.print(teamHeader[col]);
        }
        OUT.print(" ");
        blank(OUT);
        OUT.println();
    }

    private static void drawBoard (PrintStream OUT, int startRow, ChessBoard board,
                                   Collection<ChessMove> legalMoves) {
        if (teamColor == WHITE) {
            for (int row = 7; row > -1; row--) {
                outputRow(OUT, row, startRow, board, legalMoves);
            }
        } else {
            for (int row = 0; row < ROWS; row++) {
                outputRow(OUT, row, startRow, board, legalMoves);
            }
        }
    }

    private static void outputRow (PrintStream OUT, int row, int startRow, ChessBoard board,
                                   Collection<ChessMove> legalMoves) {
        int prefix = COLUMNS / 16;
        grayTile(OUT);
        OUT.print(SET_TEXT_COLOR_BLACK);
        OUT.print(EMPTY.repeat(prefix));
        if (teamColor == BLACK) {
            int copyBlackRow = row;
            copyBlackRow++;
            OUT.print(" ");
            OUT.print(String.valueOf(copyBlackRow));
            OUT.print(" ");
            OUT.print(EMPTY.repeat(prefix));
        } else {
            int copyWhiteRow = row;
            copyWhiteRow++;
            OUT.print(" ");
            OUT.print(String.valueOf(copyWhiteRow));
            OUT.print(" ");
            OUT.print(EMPTY.repeat(prefix));
        }
        if (teamColor == WHITE) {
            if (row % 2 == 0) {
                for (int col = 1; col <= COLUMNS; col ++) {
                    if (col % 2 == 0) {
                        placePieceWhiteTile();
                    } else {
                        placePieceBlackTile();
                    }
                }
            } else {
                for (int col = 1; col <= COLUMNS; col ++) {
                    if (col % 2 == 0) {
                        placePieceBlackTile();
                    } else {
                        placePieceWhiteTile();
                    }
                }
            }
        } else {
            int rowCopy = row;
            rowCopy++;
            if (rowCopy % 2 == 0) {
                for (int col = 8; col > 0; col--) {
                    if (col % 2 == 0) {
                        placePieceWhiteTile();
                    }else{
                        placePieceBlackTile();
                    }
                }
            } else {
                for (int col = 8; col > 0; col--) {
                    int colCopy = col;
                    colCopy--;
                    if (colCopy % 2 == 0) {
                        placePieceBlackTile();
                    }else{
                        placePieceWhiteTile();
                    }
                }
            }
        }
        grayTile(OUT);
        OUT.print(EMPTY.repeat(prefix));
        OUT.print(SET_TEXT_COLOR_BLACK);
        if (teamColor == BLACK) {
            int copyWhiteRow = row + 1;
            OUT.print(" ");
            OUT.print(String.valueOf(copyWhiteRow));
            OUT.print(" ");
            OUT.print(EMPTY.repeat(prefix));
        } else {
            int copyBlackRow = row + 1;
            OUT.print(" ");
            OUT.print(String.valueOf(copyBlackRow));
            OUT.print(" ");
            OUT.print(EMPTY.repeat(prefix));
        }
        blank(OUT);
        OUT.print(EMPTY.repeat(prefix));
        OUT.println();
        if (row == 0 && teamColor == WHITE) {
            grayTile(OUT);
            OUT.print(" ");
            outputHeaders(OUT, whiteTeamHeader);
        } else if (row == 7 && teamColor == BLACK) {
            grayTile(OUT);
            OUT.print(" ");
            outputHeaders(OUT, blackTeamHeader);
        }
    }

    private static void placePieceWhiteTile(int row, int col, int prefix, PrintStream OUT,
                                            ChessBoard board, Collection<ChessMove> legalMoves) {
        col--;
        whiteTile(OUT);
        OUT.print(EMPTY.repeat(prefix));
        ChessPiece chessPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
        checkMoves(legalMoves, row, col, OUT);
        if (chessPiece != null) {
            String currentPiece = getPiece(chessPiece, null, OUT);
            OUT.print(currentPiece);
            OUT.print(EMPTY.repeat(prefix));
        } else {
            OUT.print(EMPTY);
            OUT.print(EMPTY.repeat(prefix));
        }
    }

    private static void placePieceBlackTile (int row, int col, int prefix, PrintStream OUT,
                                             ChessBoard board, Collection<ChessMove> legalMoves) {
        col--;
        blackTile();
        OUT.print(EMPTY.repeat(prefix));
        ChessPiece chessPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
        checkMoves(legalMoves, row, col, OUT);
        if (chessPiece != null) {
            String currentPiece = getPiece(chessPiece, null, OUT);
            OUT.print(currentPiece);
            OUT.print(EMPTY.repeat(prefix));
        } else {
            OUT.print(EMPTY);
            OUT.print(EMPTY.repeat(prefix));
        }
    }

    private static void checkMoves (Collection<ChessMove> legalMoves, int row, int col, PrintStream OUT) {
        if (legalMoves != null) {
            for (ChessMove move : legalMoves) {
                if (move.getEndPosition().equals(new ChessPosition(row + 1, col +1))) {
                    OUT.print(SET_BG_COLOR_GREEN);
                    OUT.print(SET_TEXT_COLOR_RED);
                }
            }
        }
    }

    private static String getPiece (ChessPiece chessPiece, String currentPiece, PrintStream OUT) {
        if (chessPiece.getTeamColor() == BLACK) {
            return pieceTypeSwitchBLACK(chessPiece, currentPiece, OUT);
        } else {
            return pieceTypeSwitchWHITE(chessPiece, currentPiece, OUT);
        }
    }

    private static String pieceTypeSwitchBLACK (ChessPiece chessPiece, String currentPiece,
                                                PrintStream OUT) {
    return typeSwitch(chessPiece, currentPiece, OUT, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT,
            BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, SET_TEXT_COLOR_RED);
    }

    private static String pieceTypeSwitchWHITE (ChessPiece chessPiece, String currentPiece,
                                                PrintStream OUT) {
        return typeSwitch(chessPiece, currentPiece, OUT, WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT,
                WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, SET_TEXT_COLOR_BLUE);
    }

    private static String typeSwitch (ChessPiece chessPiece, String currentPiece, PrintStream OUT,
                                      String newPawn, String newRook, String newKnight,
                                      String newBishop, String newKing, String newQueen,
                                      String textColor) {
        switch (chessPiece.getPieceType()) {
            case PAWN -> currentPiece = newPawn;
            case ROOK -> currentPiece = newRook;
            case KNIGHT -> currentPiece = newKnight;
            case BISHOP -> currentPiece = newBishop;
            case KING -> currentPiece = newKing;
            case QUEEN -> currentPiece = newQueen;
        }
        OUT.print(textColor);
        return currentPiece;
    }
}
