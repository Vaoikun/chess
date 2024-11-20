package ui;

import chess.*;

import java.io.PipedReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.*;

public class BoardUI {
    private static final int COLUMNS = 8;
    private static final int ROWS = 8;

    private static int copyRowNumber;
    private static ChessBoard board = new ChessBoard();
    public static ChessGame.TeamColor color;

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);
    }


    public static void callWhiteBoard(PrintStream out, ChessBoard board, Collection<ChessMove> validMoves) {
        color = WHITE;
        int startRowNumberWhite = 1;
        String[] lettersInHeaderWhite = {"a", "b", "c", "d", "e", "f", "g", "h"};
        setGray(out);
        out.print(" ");
        drawHeaders(out, lettersInHeaderWhite);
        drawBoard(out, startRowNumberWhite, board, validMoves);
        out.println(RESET_BG_COLOR);
        out.println(RESET_TEXT_COLOR);
    }

    public static void callBlackBoard(PrintStream out, ChessBoard board, Collection<ChessMove> validMoves) {
        color = BLACK;
        int startRowNumberBlack = 8;
        String[] lettersInHeaderBlack = {"h", "g", "f", "e", "d", "c", "b", "a"};
        setGray(out);
        out.print(" ");
        drawHeaders(out, lettersInHeaderBlack);
        drawBoard(out, startRowNumberBlack, board, validMoves);
        out.println(RESET_BG_COLOR);
        out.println(RESET_TEXT_COLOR);
    }
    private static void drawHeaders(PrintStream out, String[] lettersInHeader) {
        setGray(out);
        out.print(" ");
        for(int column = 0; column < COLUMNS; column++) {
            drawHeader(out, lettersInHeader[column]);
        }
        out.print("    ");
        setBlank(out);
        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        out.print("\u2003 ");
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String letter) {
        //out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(letter);
    }

    private static void drawBoard(PrintStream out, int startRowNumber, ChessBoard board, Collection<ChessMove> validMoves) {
        if (color == WHITE) {
            for (int boardRow = 7; boardRow > -1; boardRow--) {
                drawEachRow(out, boardRow, startRowNumber, board, validMoves);
            }
        } else {
            for (int boardRow = 0; boardRow < ROWS; boardRow++) {
                drawEachRow(out, boardRow, startRowNumber, board, validMoves);
            }
        }
    }

    private static void putPieceOnWhiteSpot(int squareRow, int boardCol, int prefixLength, PrintStream out,
                                            ChessBoard board, Collection<ChessMove> validMoves) {
        boardCol--;
        setWhite(out);
        out.print(EMPTY.repeat(prefixLength));
        ChessPiece targetPiece = board.getPiece(new ChessPosition(squareRow + 1, boardCol + 1));

        checkValidMoves(validMoves, squareRow, boardCol, out);

        if (targetPiece != null) {
            String returnedPiece = pickPiece(targetPiece, null, out);
            out.print(returnedPiece);
            out.print(EMPTY.repeat(prefixLength));
        } else {
            out.print(EMPTY);
            out.print(EMPTY.repeat(prefixLength));
        }
    }

    private static void putPieceOnBlackSpot(int squareRow, int boardCol, int prefixLength, PrintStream out,
                                            ChessBoard board, Collection<ChessMove> validMoves) {
        boardCol--;
        setBlack(out);
        out.print(EMPTY.repeat(prefixLength));
        ChessPiece targetPiece = board.getPiece(new ChessPosition(squareRow + 1, boardCol + 1));

        checkValidMoves(validMoves, squareRow, boardCol, out);

        if (targetPiece != null) {
            String returnedPiece = pickPiece(targetPiece, null, out);
            out.print(returnedPiece);
            out.print(EMPTY.repeat(prefixLength));

        } else {
            out.print(EMPTY);
            out.print(EMPTY.repeat(prefixLength));
        }
    }

    private static void checkValidMoves(Collection<ChessMove> validMoves, int squareRow, int boardCol,
                                        PrintStream out) {
        if (validMoves != null) {
            for (ChessMove eachMove : validMoves) {
                if (eachMove.getEndPosition().equals(new ChessPosition(squareRow + 1, boardCol + 1))) {
                    out.print(SET_BG_COLOR_GREEN);
                    out.print(SET_TEXT_COLOR_RED);
                }
            }
        }
    }

    private static String switchTypeToGetPieceBLACK(ChessPiece targetPiece, String pieceOnUIBoard,
                                                    PrintStream out) {
        return switchPieceType(targetPiece, pieceOnUIBoard, out,
                BLACK_PAWN, BLACK_KNIGHT, BLACK_ROOK, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, SET_TEXT_COLOR_RED);
    }

    private static String switchTypeToGetPieceWHITE(ChessPiece targetPiece, String pieceOnUIBoard,
                                                    PrintStream out) {
        return switchPieceType(targetPiece, pieceOnUIBoard, out,
                WHITE_PAWN, WHITE_KNIGHT, WHITE_ROOK, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, SET_TEXT_COLOR_BLUE);
    }

    private static String switchPieceType(ChessPiece targetPiece, String pieceOnUIBoard, PrintStream out,
                                          String newPawn, String newKnight, String newRook, String newQueen, String newKing, String newBishop, String setTextColorBlue) {
        switch (targetPiece.getPieceType()) {
            case PAWN -> pieceOnUIBoard = newPawn;
            case KNIGHT -> pieceOnUIBoard = newKnight;
            case ROOK -> pieceOnUIBoard = newRook;
            case QUEEN -> pieceOnUIBoard = newQueen;
            case KING -> pieceOnUIBoard = newKing;
            case BISHOP -> pieceOnUIBoard = newBishop;
        }
        out.print(setTextColorBlue);
        return pieceOnUIBoard;
    }

    private static String pickPiece(ChessPiece targetPiece, String pieceOnUIBoard, PrintStream out) {
        if (targetPiece.getTeamColor() == BLACK) {
            return  switchTypeToGetPieceBLACK(targetPiece, pieceOnUIBoard, out);
        } else {
            return switchTypeToGetPieceWHITE(targetPiece, pieceOnUIBoard, out);
        }
    }
    private static void drawEachRow(PrintStream out, int boardRow, int startRowNumber, ChessBoard board,
                                    Collection<ChessMove> validMoves) {
        int prefixLength = (COLUMNS /16);
        setGray(out);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(EMPTY.repeat(prefixLength));
        if (color == BLACK) {
            int copyWhite = boardRow;
            copyWhite++;
            out.print(" ");
            out.print(String.valueOf(copyWhite));
            out.print(" ");
            out.print(EMPTY.repeat(prefixLength));}
        else {
            int copyRowBlack = boardRow;
            copyRowBlack++;
            out.print(" ");
            out.print(String.valueOf(copyRowBlack));
            out.print(" ");
            out.print(EMPTY.repeat(prefixLength));}
        if (color == WHITE) {
            if (boardRow % 2 == 0) {
                for (int boardCol = 1; boardCol <= COLUMNS; boardCol++) {
                    if (boardCol % 2 != 0) {
                        putPieceOnWhiteSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    } else {
                        putPieceOnBlackSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    }
                }
            } else {
                for (int boardCol = 1; boardCol <= COLUMNS; boardCol++) {
                    if (boardCol % 2 != 0) {
                        putPieceOnBlackSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    } else {
                        putPieceOnWhiteSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    }
                }
            }
        } else {
            int copyRow = boardRow;
            copyRow++;
            if (copyRow % 2 == 0) {
                for (int boardCol = 8; boardCol > 0; boardCol--) {
                    if (boardCol % 2 == 0) {
                        putPieceOnWhiteSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    } else {
                        putPieceOnBlackSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    }
                }
            } else {
                for (int boardCol = 8; boardCol > 0; boardCol--) {
                    int copyCol = boardCol;
                    copyCol--;
                    if (copyCol % 2 != 0) {
                        putPieceOnBlackSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    } else {
                        putPieceOnWhiteSpot(boardRow, boardCol, prefixLength, out, board, validMoves);
                    }
                }
            }
        }
        setGray(out);
        out.print(EMPTY.repeat(prefixLength));
        out.print(SET_TEXT_COLOR_BLACK);
        if (color == BLACK) {
            int copyRowWhite = boardRow + 1;
            out.print(" ");
            out.print(String.valueOf(copyRowWhite));
            out.print(" ");
            out.print(EMPTY.repeat(prefixLength));
        } else {
            int copyRowBlack = boardRow;
            copyRowBlack++;
            out.print(" ");
            out.print(String.valueOf(copyRowBlack));
            out.print(" ");
        }
        setBlank(out);
        out.print(EMPTY.repeat(prefixLength));
        out.println();
        if (boardRow == 0 && color == WHITE) {
            setGray(out);
            out.print(" ");
            String[] lettersInHeaderWhite = {"a", "b", "c", "d", "e", "f", "g", "h"};
            drawHeaders(out, lettersInHeaderWhite);
        } else if (boardRow == 7 && color == BLACK) {
            out.print(" ");
            String[] lettersInHeaderBlack = {"h", "g", "f", "e", "d", "c", "b", "a"};
            drawHeaders(out, lettersInHeaderBlack);
        }
    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlank(PrintStream out) {
        out.print(RESET_BG_COLOR);
    }
}