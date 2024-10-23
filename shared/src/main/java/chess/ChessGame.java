package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame{

    private ChessBoard board;

    public TeamColor turn;

    public boolean isResigned = false;

    public ChessGame() {
        this.board = new ChessBoard(); // get the ChessBoard by creating a Chessboard object by constructor
        this.board.resetBoard(); // reset The board;
        turn = TeamColor.WHITE; // the default turn is white
    }

    public ChessGame(ChessBoard board, TeamColor turn)
    {
        this.board = board;
        turn  = turn;

    }


    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team; // set the turn color to the team
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currentPiece = this.board.getPiece(startPosition);
        if(currentPiece == null)
        {
            return null;
        }
        Collection<ChessMove> potentialMoves;
        HashSet<ChessMove> resultValid = new HashSet<>();
        try {
            potentialMoves = currentPiece.pieceMoves(this.board, startPosition); // get all potential moves but need to plus isinCheck
            for (ChessMove smallMove : potentialMoves) {
                ChessBoard newBoard = (ChessBoard) this.board.clone();
                newBoard.addPiece(smallMove.startPosition, null);
                newBoard.addPiece(smallMove.endPosition, currentPiece);
                ChessGame checkedGame = new ChessGame(newBoard, turn);

                if (!checkedGame.isInCheck(currentPiece.getTeamColor())) {
                    resultValid.add(smallMove);
                }
                // checkedGame.board.resetBoard();

            }
        }
        catch(CloneNotSupportedException E)
        {
            System.err.println(E.getMessage());
        }
        return resultValid;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // call validMove to get all validMoves

        Collection<ChessMove> validMoves = validMoves(move.startPosition);
        ChessPiece startPiece = this.board.getPiece(move.getStartPosition());

        if (validMoves == null)
        {
            throw new InvalidMoveException("Valid Moves are null");
        }
        //making move
        if (validMoves.contains(move) && startPiece.getTeamColor() == this.turn) {
            this.board.addPiece(move.startPosition, null);
            if (move.getPromotionPiece() == null) {
                this.board.addPiece(move.endPosition, startPiece); // regular move
            } else // need to promote
            {
                // get the promotedPiece
                ChessPiece promotedPiece = new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece());
                this.board.addPiece(move.endPosition, promotedPiece);
            }

            // changed the turn
            if (this.turn == TeamColor.WHITE) {
                this.turn = TeamColor.BLACK;
            } else {
                this.turn = TeamColor.WHITE;
            }
        } else {
            throw new InvalidMoveException("not valid move.");
        }


    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessBoard currentBoard = this.getBoard(); // get the currentBoard
        ChessPosition kingPosition = null;

        // to get the King's position
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = currentBoard.getPiece(currentPosition); // get the currentPiece
                if (currentPiece == null)
                {
                    continue;
                }
                if (currentPiece.getPieceType() == ChessPiece.PieceType.KING && currentPiece.getTeamColor() == teamColor) {
                    kingPosition = currentPosition; // get the King's position
                    break;
                }
            }
        }

        // to make sure the current spot's end position is the King's position
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = currentBoard.getPiece(currentPosition); // get the currentPiece
                if (currentPiece == null)
                {
                    continue;
                }
                if (currentPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> allMoves = currentPiece.pieceMoves(currentBoard, currentPosition);
                    for (ChessMove smallMove : allMoves) {
                        if (smallMove.endPosition.equals(kingPosition))
                        {
                            return true;
                        }
                    }
                }

            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> canValidMoves;
        // call isInCheck. IS TRUE
        if (isInCheck(teamColor))
        {
            // call valid moves, which is empty
            for (int row = 0; row < 8; row++)
            {
                for (int col = 0; col < 8; col++)
                {
                    ChessPosition currentPosition = new ChessPosition(row + 1, col + 1);
                    ChessPiece currentPiece = this.board.getPiece(currentPosition);
                    if (currentPiece != null)
                    {
                        if (currentPiece.getTeamColor() == teamColor) {
                            try
                            {
                                canValidMoves = validMoves(currentPosition);
                                if (!canValidMoves.isEmpty()) {
                                    return false;
                                }
                            }
                            catch (RuntimeException e)
                            {
                                System.err.println("Invalid Move");
                            }

                        }
                    }

                }
            }
            return true;
        }
        else
        {
            return false;
        }

        // is true
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // it is only called
        Collection<ChessMove> validMoves;
        // call isInCheck. IS TRUE
        boolean isStaleMate = true;
        if (!isInCheck(teamColor))
        {
            while(true)
            {
                if ( this.turn == teamColor)
                {
                    // call valid moves, which is empty
                    for (int row = 0; row < 8; row++)
                    {
                        for (int col = 0; col < 8; col++) {
                            ChessPosition currentPosition = new ChessPosition(row + 1, col + 1);
                            ChessPiece currentPiece = this.board.getPiece(currentPosition);
                            if (currentPiece != null && currentPiece.getTeamColor() == teamColor)
                            {
                                try
                                {
                                    validMoves = validMoves(currentPosition);
                                    if (!validMoves.isEmpty())
                                    {
                                        isStaleMate = false;
                                    }
                                    if (this.turn == TeamColor.WHITE)
                                    {
                                        this.turn = TeamColor.BLACK;
                                    }
                                    else
                                    {
                                        this.turn = TeamColor.WHITE;
                                    }
                                }
                                catch ( RuntimeException e)
                                {
                                    System.err.println(e.getMessage());
                                }

                            }
                        }
                    }
                    if (!isStaleMate)
                    {
                        return isStaleMate;
                    }
                    break;
                }
                else
                {
                    if (this.turn == TeamColor.WHITE)
                    {
                        this.turn = TeamColor.BLACK;
                    }
                    else
                    {
                        this.turn = TeamColor.WHITE;
                    }
                }
            }
            return isStaleMate;
            // is true
        }
        else
        {
            return false;
        }


    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public String toString() {
        return "ChessGame{}";
    }
}