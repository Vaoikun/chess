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
    private static final String[] BLACK_TEAM_HEADER = {"h", "g", "f", "e", "d", "c", "b", "a"};
    private static final String[] WHITE_TEAM_HEADER = {"a", "b", "c", "d", "e", "f", "g", "h"};
    public static ChessGame.TeamColor teamColor;

    public static void main (String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
    }

    private static void grayTile (PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }
    private static void whiteTile (PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void blackTile (PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void blank (PrintStream out) {
        out.print(RESET_TEXT_COLOR);
        out.print(RESET_BG_COLOR);
    }

    public static void callWhiteTiles (PrintStream out, ChessBoard board, Collection<ChessMove> legalMoves) {
        teamColor = WHITE;
        int startRow = 1;
        grayTile(out);
        out.print(" ");
        outputHeaders(out, WHITE_TEAM_HEADER);
        drawBoard(out, startRow, board, legalMoves);
        out.println(RESET_BG_COLOR);
        out.println(RESET_TEXT_COLOR);
    }

    public static void callBlackTiles (PrintStream out, ChessBoard board, Collection<ChessMove> legalMoves) {
        teamColor = BLACK;
        int startRow = 1;
        grayTile(out);
        out.print(" ");
        outputHeaders(out, BLACK_TEAM_HEADER);
        drawBoard(out, startRow, board, legalMoves);
        out.println(RESET_BG_COLOR);
        out.println(RESET_TEXT_COLOR);
    }

    private static void outputHeaders (PrintStream out, String[] teamHeader) {
        grayTile(out);
        out.print(" ");
        for (int col =0; col < COLUMNS; col++) {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(" ");
            out.print(teamHeader[col]);
        }
        out.print("    ");
        blank(out);
        out.println();
    }

    private static void drawBoard (PrintStream out, int startRow, ChessBoard board,
                                   Collection<ChessMove> legalMoves) {
        if (teamColor == WHITE) {
            for (int row = 7; row > -1; row--) {
                outputRow(out, row, startRow, board, legalMoves);
            }
        } else {
            for (int row = 0; row < ROWS; row++) {
                outputRow(out, row, startRow, board, legalMoves);
            }
        }
    }

    private static void outputRow (PrintStream out, int row, int startRow, ChessBoard board,
                                   Collection<ChessMove> legalMoves) {
        int prefix = COLUMNS / 16;
        grayTile(out);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY.repeat(prefix));
        if (teamColor == BLACK) {
            int copyWhiteRow = row;
            copyWhiteRow++;
            out.print(" ");
            out.print(String.valueOf(copyWhiteRow));
            out.print(" ");
            out.print(EMPTY.repeat(prefix));
        } else {
            int copyBlackRow = row;
            copyBlackRow++;
            out.print(" ");
            out.print(String.valueOf(copyBlackRow));
            out.print(" ");
            out.print(EMPTY.repeat(prefix));
        }
        if (teamColor == WHITE) {
            if (row % 2 == 0) {
                for (int col = 1; col <= COLUMNS; col ++) {
                    placePiece(row, col, prefix, out, board, legalMoves, true);
                }
            } else {
                for (int col = 1; col <= COLUMNS; col ++) {
                    placePiece(row, col, prefix, out, board, legalMoves, false);
                }
            }
        } else {
            int rowCopy = row;
            rowCopy++;
            if (rowCopy % 2 == 0) {
                for (int col = 8; col > 0; col--) {
                    placePiece(row, col, prefix, out, board, legalMoves, false);
                }
            } else {
                for (int col = 8; col > 0; col--) {
                    placePiece(row, col, prefix, out, board, legalMoves, false);
                }
            }
        }
        grayTile(out);
        out.print(EMPTY.repeat(prefix));
        out.print(SET_TEXT_COLOR_BLACK);
        if (teamColor == BLACK) {
            int copyWhiteRow = row + 1;
            out.print(" ");
            out.print(String.valueOf(copyWhiteRow));
            out.print(" ");
            out.print(EMPTY.repeat(prefix));
        } else {
            int copyBlackRow = row + 1;
            out.print(" ");
            out.print(String.valueOf(copyBlackRow));
            out.print(" ");
        }
        blank(out);
        out.print(EMPTY.repeat(prefix));
        out.println();
        if (row == 0 && teamColor == WHITE) {
            grayTile(out);
            out.print(" ");
            outputHeaders(out, WHITE_TEAM_HEADER);
        } else if (row == 7 && teamColor == BLACK) {
            grayTile(out);
            out.print(" ");
            outputHeaders(out, BLACK_TEAM_HEADER);
        }
    }

    private static void placePiece (int row, int col, int prefix, PrintStream out,
                                    ChessBoard board, Collection<ChessMove> legalMoves, boolean evenRow) {
        if (evenRow) {
            if (col % 2 == 0) {
                placePieceWhiteTile(row, col, prefix, out, board, legalMoves);
            } else {
                placePieceBlackTile(row, col, prefix, out, board, legalMoves);
            }
        } else {
            if (col % 2 == 0) {
                placePieceBlackTile(row, col, prefix, out, board, legalMoves);
            } else {
                placePieceWhiteTile(row, col, prefix, out, board, legalMoves);
            }
        }
    }

    private static void placePieceWhiteTile(int row, int col, int prefix, PrintStream out,
                                            ChessBoard board, Collection<ChessMove> legalMoves) {
        col--;
        whiteTile(out);
        out.print(EMPTY.repeat(prefix));
        ChessPiece chessPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
        checkMoves(legalMoves, row, col, out);
        if (chessPiece != null) {
            String currentPiece = getPiece(chessPiece, null, out);
            out.print(currentPiece);
            out.print(EMPTY.repeat(prefix));
        } else {
            out.print(EMPTY);
            out.print(EMPTY.repeat(prefix));
        }
    }

    private static void placePieceBlackTile (int row, int col, int prefix, PrintStream out,
                                             ChessBoard board, Collection<ChessMove> legalMoves) {
        col--;
        blackTile(out);
        out.print(EMPTY.repeat(prefix));
        ChessPiece chessPiece = board.getPiece(new ChessPosition(row + 1, col + 1));
        checkMoves(legalMoves, row, col, out);
        if (chessPiece != null) {
            String currentPiece = getPiece(chessPiece, null, out);
            out.print(currentPiece);
            out.print(EMPTY.repeat(prefix));
        } else {
            out.print(EMPTY);
            out.print(EMPTY.repeat(prefix));
        }
    }

    private static void checkMoves (Collection<ChessMove> legalMoves, int row, int col, PrintStream out) {
        if (legalMoves != null) {
            for (ChessMove move : legalMoves) {
                if (move.getEndPosition().equals(new ChessPosition(row + 1, col +1))) {
                    out.print(SET_BG_COLOR_GREEN);
                    out.print(SET_TEXT_COLOR_RED);
                }
            }
        }
    }

    private static String getPiece (ChessPiece chessPiece, String currentPiece, PrintStream out) {
        if (chessPiece.getTeamColor() == BLACK) {
            return pieceTypeSwitchBLACK(chessPiece, currentPiece, out);
        } else {
            return pieceTypeSwitchWHITE(chessPiece, currentPiece, out);
        }
    }

    private static String pieceTypeSwitchBLACK (ChessPiece chessPiece, String currentPiece,
                                                PrintStream out) {
    return typeSwitch(chessPiece, currentPiece, out, BLACK_PAWN, BLACK_ROOK, BLACK_KNIGHT,
            BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, SET_TEXT_COLOR_RED);
    }

    private static String pieceTypeSwitchWHITE (ChessPiece chessPiece, String currentPiece,
                                                PrintStream out) {
        return typeSwitch(chessPiece, currentPiece, out, WHITE_PAWN, WHITE_ROOK, WHITE_KNIGHT,
                WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, SET_TEXT_COLOR_BLUE);
    }

    private static String typeSwitch (ChessPiece chessPiece, String currentPiece, PrintStream out,
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
        out.print(textColor);
        return currentPiece;
    }
}
