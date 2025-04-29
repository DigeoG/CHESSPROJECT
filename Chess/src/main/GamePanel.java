package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import ai.ChessAI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    // PIECES
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;
    public boolean againstAI;
    public ChessAI chessAI;

    // BOOLEANS
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    boolean validHumanMoveMade = false;
    public Piece promotingPawn = null;



public GamePanel(boolean againstAI) {
    this.againstAI = againstAI;
    
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    setBackground(Color.black);
    addMouseMotionListener(mouse);
    addMouseListener(mouse);

    setPieces();
    copyPieces(pieces, simPieces);

    if (this.againstAI) {
        System.out.println("AI mode detected! Initializing ChessAI...");
        new Thread(() -> {  // Run ChessAI initialization in a separate thread
            try {
                chessAI = new ChessAI( "C:/Users/David Ozowara/Documents/NetBeansProjects/Chesstt/stockfish-windows-x86-64-avx2.exe");
                System.out.println("ChessAI successfully initialized.");
            } catch (Exception e) {
                System.out.println("Error initializing ChessAI: " + e.getMessage());
            }
        }).start(); 
//        "C:/Users/David Ozowara/Documents/NetBeansProjects/Chesstt/stockfish-windows-x86-64-avx2.exe"
    } else {
        System.out.println("againstAI is false in GamePanel: ChessAI is not initialized.");
    }
}


    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        // WHITE TEAM
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        // BLACK TEAM
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }

    public void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();

        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    @Override
    public void run() {
        // GAME LOOP
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }
    
public void update() {
    if (promotion) {
        promoting();
    } else if (!gameover && !stalemate) {
        
    if (currentColor == WHITE && againstAI) {
        isCurrentKingInCheck();
        

        if (isCheckMate()) {
            System.out.println("CHECKMATE detected during update()");
            gameover = true;
            repaint();
        }
    }
    

    

        
        //// MOUSE PRESSED - pick up piece ////
        if (mouse.pressed) {
            if (activeP == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor &&
                        piece.col == mouse.x / Board.SQUARE_SIZE &&
                        piece.row == mouse.y / Board.SQUARE_SIZE) {
                        activeP = piece;
                    }
                }
            } else {
                // Optional: update preview during drag
                simulate();
            }
        }

        //// MOUSE RELEASED - drop and validate ////
        if (!mouse.pressed) {
            if (activeP != null) {
                if (validSquare) {
                    validHumanMoveMade = true;

                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    System.out.println("Human moved piece to: col = " + activeP.col + ", row = " + activeP.row);

                    if (castlingP != null) {
                        castlingP.updatePosition();
                    }

                    if (isKingInCheck() && isCheckMate()) {
                        gameover = true;
                    } else if (isStalemate() && !isKingInCheck()) {
                        stalemate = true;
                    } else if (canPromote()) {
                        promotion = true;
                        promotingPawn = activeP; 
                    } else {
                        changePlayer(); // switch to BLACK
                        repaint();

                        // ⛔️ Delay AI turn until the *next* update() frame
                        SwingUtilities.invokeLater(() -> aiTurnPending = true);

                    }

                } else {
                    // Invalid move, reset the piece
                    System.out.println("Invalid human move attempted: " + activeP.col + "," + activeP.row);
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }

                // Always clear piece selection after mouse release
                activeP = null;
            }
        }

        //// AI MOVE (one-time) ////
    if (validHumanMoveMade && aiTurnPending && !promotion && !gameover && !stalemate) {
        makeAIMove();
        aiTurnPending = false;
        validHumanMoveMade = false; // Reset for next move
        
    }

    }
}


    public void simulate() {
        canMove = false;
        validSquare = false;

        // Reset all the pieces in every loop
        // Restoring the position during simulation phase
        copyPieces(pieces, simPieces);

        // Reset the castling piece position
        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        // If a piece is being held, update its position
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        // Check if the piece is hovering over a reachable square
        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            // If hitting a piece, remove the piece from the list
            if (activeP.hittingP != null) {
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            if (isIllegal(activeP) == false && opponentCanCaptureKing() == false) {
                validSquare = true;
            }
        }
        repaint();
    }

    public boolean isIllegal(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean opponentCanCaptureKing() {
        Piece king = getKing(false);

        for (Piece piece : simPieces) {
            if (piece.color != king.color && piece.canMove(king.col, king.row)) {
                return true;
            }
        }

        return false;
    }

    public boolean isKingInCheck() {

        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) {
            checkingP = activeP;
            return true;
        } else {
            checkingP = null;
        }

        return false;
    }
    
public boolean isCurrentKingInCheck() {
    
Piece king = getKing(false); // current player’s king
for (Piece p : simPieces) {
    if (p.color != king.color && p.canMove(king.col, king.row)) {
        checkingP = p;
        //System.out.println("? King is in check from: " + p.getClass().getSimpleName() + " at " + p.col + "," + p.row);
        return true;
    }
}
checkingP = null;
//System.out.println("?? checkingP is null – not in check?");
return false;


//    Piece king = getKing(false);  // Get the current player's king
//
//    for (Piece piece : simPieces) {
//        if (piece.color != king.color && piece.canMove(king.col, king.row)) {
//            checkingP = piece;  // 🟥 Set the correct checking piece!
//            return true;
//        }
//    }
//
//    checkingP = null;
//    return false;
}






    public Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }

        return king;
    }

    public boolean isCheckMate() {
        
        if (checkingP == null) return false;


        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        } else {
            // The player still had a chance
            // Check if he can block attack with his pieces

            // Check the position of the checking piece and the king in check
            int colDiff = Math.abs(checkingP.col - king.col);
            int rowDiff = Math.abs(checkingP.row - king.row);

            if (colDiff == 0) {
                // The checking piece is attacking vertically
                if (checkingP.row < king.row) {
                    // The checking piece is above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }

                if (checkingP.row > king.row) {
                    // The checking piece is below the king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }

            } else if (rowDiff == 0) {
                // The checking piece is attacking horizontally
                if (checkingP.col < king.col) {
                    // The checking piece is to the left
                    for (int col = checkingP.col; col < king.row; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }

                if (checkingP.col > king.col) {
                    // The checking piece is to the right
                    for (int col = checkingP.col; col > king.row; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            } else if (colDiff == rowDiff) {
                // The checking piece is attacking diagonally
                if (checkingP.row < king.row) {
                    // The checking piece is above the king
                    if (checkingP.col < king.col) {
                        // The checking piece is in the upper left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                    if (checkingP.col > king.col) {
                        // The checking piece is in the upper right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (checkingP.row > king.row) {
                    // The checking piece is below the king
                    if (checkingP.col < king.col) {
                        // The checking piece is in the lower left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                    if (checkingP.col > king.col) {
                        // The checking piece is in the lower right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            } else {
                // The checking place is knight
            }
        }

        return true;
    }

    public boolean kingCanMove(Piece king) {

        // Simulate if there is a square where the king can move
        if (isValidMove(king, -1, -1)) {
            return true;
        }
        if (isValidMove(king, 0, -1)) {
            return true;
        }
        if (isValidMove(king, 1, -1)) {
            return true;
        }
        if (isValidMove(king, -1, 0)) {
            return true;
        }
        if (isValidMove(king, 1, 0)) {
            return true;
        }
        if (isValidMove(king, -1, 1)) {
            return true;
        }
        if (isValidMove(king, 0, 1)) {
            return true;
        }
        if (isValidMove(king, 1, 1)) {
            return true;
        }

        return false;
    }

    public boolean isValidMove(Piece king, int colPlus, int rowPlus) {
        boolean isValidMove = false;

        // Update the temporary King position
        king.col += colPlus;
        king.row += rowPlus;

        if (king.canMove(king.col, king.row)) {
            if (king.hittingP != null) {
                simPieces.remove(king.hittingP.getIndex());
            }

            if (isIllegal(king) == false) {
                isValidMove = true;
            }
        }

        // Reset the temporary King position
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    public boolean isStalemate() {
        int count = 0;

        // Count the number of pieces
        for (Piece piece : simPieces) {
            if (piece.color != currentColor) {
                count++;
            }
        }

        // If only one piece is left (king)
        if (count == 1) {
            if (kingCanMove(getKing(true)) == false) {
                return true;
            }
        }

        return false;
    }

    public void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) {
                castlingP.col += 3;
            } else if (castlingP.col == 7) {
                castlingP.col -= 2;
            }

            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

public boolean aiTurnPending = false;  // add this in your class


public void changePlayer() {
    if (currentColor == WHITE) {
        currentColor = BLACK;

        for (Piece piece : pieces) {
            if (piece.color == BLACK) {
                piece.twoStepped = false;
            }
        }

        if (againstAI) {
            aiTurnPending = true;  // ✅ trigger AI move in next update
        }

    } else {
        currentColor = WHITE;

        for (Piece piece : pieces) {
            if (piece.color == WHITE) {
                piece.twoStepped = false;
            }
        }

        // No AI trigger here — wait for human
    }

    activeP = null;
}



// This is your updated makeAIMove method with the correct order and checkmate detection.
public void makeAIMove() {
    if (chessAI == null) {
        System.out.println("Error: ChessAI is not initialized.");
        return;
    }

    System.out.println("AI is making its move...");

    String fenBeforeMove = getFEN();
    System.out.println("Sending FEN to AI: " + fenBeforeMove);

    String bestMove = chessAI.getBestMove(fenBeforeMove);
    System.out.println("Best move received from AI: " + bestMove);

    applyMove(bestMove);
    System.out.println("Move applied: " + bestMove);

    // Switch to white before updating/checking
    currentColor = WHITE;

    // Update FEN and board state
    String fenAfterMove = updateFenAfterMove(fenBeforeMove, bestMove);
    System.out.println("Updated FEN after AI move: " + fenAfterMove);
    updateBoardFromFen(fenAfterMove);
    System.out.println("Board updated from new FEN.");

    // Rebuild simPieces from pieces if needed here
    copyPieces(pieces, simPieces);

    isCurrentKingInCheck();

    if (checkingP != null) {
        //System.out.println("CHECK: Human king is in check from " + checkingP.getClass().getSimpleName() +
         //   " at col = " + checkingP.col + ", row = " + checkingP.row);

        if (isCheckMate()) {
            System.out.println("\u2705 Checkmate detected after AI move!");
            gameover = true;
            repaint();
            return;
        }
    } else {
        System.out.println("No check after AI move.");
    }
}




public Piece getPieceAt(List<Piece> pieceList, int col, int row) {
    for (Piece piece : pieceList) {
        if (piece.col == col && piece.row == row) {
            return piece;
        }
    }
    return null;
}


public String updateFenAfterMove(String currentFen, String move) {
    // Convert move like "e2e4" to board indices
    int fromCol = move.charAt(0) - 'a';
    int fromRow = 8 - Character.getNumericValue(move.charAt(1));
    int toCol = move.charAt(2) - 'a';
    int toRow = 8 - Character.getNumericValue(move.charAt(3));

    Piece activeP = getPieceAt(fromCol, fromRow);
    if (activeP == null) {
        System.out.println("No piece at source square.");
        return currentFen;
    }

    // Copy current board to simPieces for testing
    copyPieces(pieces, simPieces);
    Piece simulatedPiece = getPieceAt(simPieces, fromCol, fromRow);
    if (simulatedPiece == null) return currentFen;

    // Attempt move
    simulatedPiece.col = toCol;
    simulatedPiece.row = toRow;

    if (!simulatedPiece.canMove(toCol, toRow) || isIllegal(simulatedPiece)) {
        System.out.println("Invalid move in FEN update: " + move);
        return currentFen;
    }

    // Valid move, now apply it to the real board
    copyPieces(simPieces, pieces);
    Piece realPiece = getPieceAt(fromCol, fromRow);
    if (realPiece != null) {
        realPiece.col = toCol;
        realPiece.row = toRow;
        realPiece.updatePosition();
    }

    // Optionally handle promotion/castling if needed
    // Optionally call changePlayer(); here if you're switching players via FEN update

    System.out.println("Move applied: " + move);
    return getFEN(); // Your custom FEN builder
}

 
 

public void updateBoardFromFen(String fen) {
    System.out.println("Updating board with FEN: " + fen); // Debugging
    pieces.clear(); // Remove existing pieces

    String[] fenParts = fen.split(" ");
    String boardState = fenParts[0];
    String[] rows = boardState.split("/");

    for (int row = 0; row < 8; row++) {
        String fenRow = rows[row];
        int col = 0;

        for (int i = 0; i < fenRow.length(); i++) {
            char c = fenRow.charAt(i);

            if (Character.isDigit(c)) {
                col += c - '0'; // Empty squares
            } else {
                Piece piece = createPieceFromFEN(c, row, col);
                pieces.add(piece);
                col++;
            }
        }
    }

    //repaint(); // Force board redraw
}


public Piece createPieceFromFEN(char c, int row, int col) {
    int color = Character.isLowerCase(c) ? GamePanel.BLACK : GamePanel.WHITE; // Determine color
    char pieceChar = Character.toUpperCase(c); // Convert to uppercase to match piece types

    // Create the correct piece based on the character
    switch (pieceChar) {
        case 'P' -> {
            return new Pawn(color, col, row);
            }
        case 'N' -> {
            return new Knight(color, col, row);
            }
        case 'B' -> {
            return new Bishop(color, col, row);
            }
        case 'R' -> {
            return new Rook(color, col, row);
            }
        case 'Q' -> {
            return new Queen(color, col, row);
            }
        case 'K' -> {
            return new King(color, col, row);
            }
        default -> throw new IllegalArgumentException("Invalid FEN character: " + c);
    }
}


    
    public String getFEN() {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = getPieceAt(col, row);
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece.getFENChar());
                }
            }
            if (emptyCount > 0) fen.append(emptyCount);
            if (row < 7) fen.append("/");
        }
        fen.append(" ").append(currentColor == WHITE ? "w" : "b");
        return fen.toString();
    }
        
        
 public void applyMove(String bestMove) {
    if (!bestMove.startsWith("bestmove")) return;

    String[] parts = bestMove.split(" ");
    if (parts.length < 2) return;  // Ensure move exists

    String move = parts[1];
    int fromCol = move.charAt(0) - 'a';
    int fromRow = 8 - (move.charAt(1) - '0');
    int toCol = move.charAt(2) - 'a';
    int toRow = 8 - (move.charAt(3) - '0');

    Piece piece = getPieceAt(fromCol, fromRow);
    if (piece != null) {
        // Remove captured piece if any
        Piece capturedPiece = getPieceAt(toCol, toRow);
        if (capturedPiece != null) {
            pieces.remove(capturedPiece);
        }

        // Move piece
        piece.col = toCol;
        piece.row = toRow;
        

        
    }
}


    public Piece getPieceAt(int col, int row) {
        for (Piece piece : pieces) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }
    
    
    


public boolean canPromote() {
    if (activeP.type == Type.PAWN) {
        if ((currentColor == WHITE && activeP.row == 0) ||
            (currentColor == BLACK && activeP.row == 7)) {

            promoPieces.clear();

            int promoCol = 8;        // right of the board
            int promoStartRow = 2;   // consistent vertical position

            promoPieces.add(new Rook(currentColor, promoCol, promoStartRow));
            promoPieces.add(new Knight(currentColor, promoCol, promoStartRow + 1));
            promoPieces.add(new Bishop(currentColor, promoCol, promoStartRow + 2));
            promoPieces.add(new Queen(currentColor, promoCol, promoStartRow + 3));

            return true;
        }
    }

    return false;
}


public void promoting() {
    if (promotingPawn == null) {
        System.out.println("⚠️ promotingPawn is null — skipping promotion.");
        promotion = false;
        return;
    }

    if (mouse.pressed) {
        for (Piece piece : promoPieces) {
            if (piece.col == mouse.x / Board.SQUARE_SIZE &&
                piece.row == mouse.y / Board.SQUARE_SIZE) {

                int promoteCol = promotingPawn.col;
                int promoteRow = promotingPawn.row;

                switch (piece.type) {
                    case ROOK:
                        simPieces.add(new Rook(currentColor, promoteCol, promoteRow));
                        break;
                    case KNIGHT:
                        simPieces.add(new Knight(currentColor, promoteCol, promoteRow));
                        break;
                    case BISHOP:
                        simPieces.add(new Bishop(currentColor, promoteCol, promoteRow));
                        break;
                    case QUEEN:
                        simPieces.add(new Queen(currentColor, promoteCol, promoteRow));
                        break;
                }

                simPieces.remove(promotingPawn.getIndex());
                copyPieces(simPieces, pieces);

                activeP = null;
                promotingPawn = null; // ✅ Clear promotion context
                promotion = false;
                changePlayer();
            }
        }
    }
}


@Override
public void paintComponent(Graphics g) 
{
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;

    // BOARD
    board.draw(g2);

    // 🔴 Highlight the current player's king if in check
    if (checkingP != null) {
        Piece king = getKing(false); // false = current player's king
        if (king != null) {
            g2.setColor(new Color(255, 0, 0, 120)); // semi-transparent red
            g2.fillRect(king.col * Board.SQUARE_SIZE,
                        king.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE);
        }
    }

    // PIECES
    for (Piece p : simPieces) {
        p.draw(g2);
    }
    
    

    // ACTIVE PIECE HIGHLIGHTING
    if (activeP != null) {
        if (canMove) {
            if (isIllegal(activeP) || opponentCanCaptureKing()) {
                g2.setColor(Color.gray);
            } else {
                g2.setColor(Color.white);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                        Board.SQUARE_SIZE, Board.SQUARE_SIZE);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Draw active piece on top
        activeP.draw(g2);
    }

    // AI TURN INDICATOR
    if (this.againstAI && currentColor == BLACK) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.drawString("AI is thinking...", 450, 350);
    }

    // TEXT STATUS (Check, Turn, Promotion)
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
    g2.setColor(Color.white);

    if (promotion) {
        g2.drawString("Promote to:", 840, 150);
        for (Piece piece : promoPieces) {
            g2.drawImage(piece.image,
                         piece.getX(piece.col),
                         piece.getY(piece.row),
                         Board.SQUARE_SIZE,
                         Board.SQUARE_SIZE,
                         null);
        }
    } else {
        if (currentColor == WHITE) {
            g2.drawString("White's turn", 840, 550);

            if (checkingP != null && checkingP.color == BLACK) {
                g2.setColor(Color.red);
                g2.drawString("The King", 840, 650);
                g2.drawString("is in check!", 840, 700);
            }
        } else {
            g2.drawString("Black's turn", 840, 250);

            if (checkingP != null && checkingP.color == WHITE) {
                g2.setColor(Color.red);
                g2.drawString("The King", 840, 100);
                g2.drawString("is in check!", 840, 150);
            }
        }
    }

    // GAME OVER OVERLAY
    if (gameover) {
        String s = (currentColor == WHITE) ? "White Wins" : "Black Wins";

        g2.setColor(new Color(0, 0, 0, 128)); // semi-transparent background
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Arial", Font.BOLD, 90));
        g2.setColor(Color.green);
        g2.drawString(s, 200, 420);
    }

    // STALEMATE OVERLAY
    if (stalemate) {
        g2.setColor(new Color(0, 0, 0, 128));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Arial", Font.BOLD, 90));
        g2.setColor(Color.lightGray);
        g2.drawString("Stalemate", 200, 420);
    }
}



public void saveGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save Game As...");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile() + ".txt")) {
            writer.write(getSaveGameModeTag() + "\n");  // ✨ First line: Game Mode Tag
            writer.write(getFEN());
            JOptionPane.showMessageDialog(this, "💾 Game saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Failed to save game: " + e.getMessage());
        }
    }
}



public void loadGame() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Load Saved Game");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

    int userSelection = fileChooser.showOpenDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
            String gameModeTag = reader.readLine();  // ✨ First line is mode
            String fen = reader.readLine();

            if (!gameModeTag.equals(getSaveGameModeTag())) {
                    JOptionPane.showMessageDialog(this, 
                        "⚠️ This save file is from a different game mode: [" + gameModeTag + "]\n" +
                        "You are currently playing in [" + getSaveGameModeTag() + "] mode.\n\n" +
                        "Please return to the Main Menu and select the correct mode to load this game.",
                        "Load Error",
                        JOptionPane.WARNING_MESSAGE
                    );
                return;
            }

            loadFromFEN(fen);
            JOptionPane.showMessageDialog(this, "📂 Game loaded successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "⚠️ Failed to load game: " + e.getMessage());
        }
    }
}



public void loadFromFEN(String fen) {
    pieces.clear();

    String[] parts = fen.split(" ");
    String boardPart = parts[0];
    String turnPart = parts[1];

    String[] rows = boardPart.split("/");

    for (int row = 0; row < 8; row++) {
        String rowString = rows[row];
        int col = 0;
        for (char c : rowString.toCharArray()) {
            if (Character.isDigit(c)) {
                col += Character.getNumericValue(c); // Empty squares
            } else {
                int color = Character.isUpperCase(c) ? WHITE : BLACK;
                Piece newPiece = createPieceFromFENChar(c, color, col, row);
                if (newPiece != null) {
                    pieces.add(newPiece);
                }
                col++;
            }
        }
    }

    currentColor = turnPart.equals("w") ? WHITE : BLACK;
    copyPieces(pieces, simPieces); // Sync board
    repaint();
}

private Piece createPieceFromFENChar(char c, int color, int col, int row) {
    char lower = Character.toLowerCase(c);
    return switch (lower) {
        case 'p' -> new Pawn(color, col, row);
        case 'r' -> new Rook(color, col, row);
        case 'n' -> new Knight(color, col, row);
        case 'b' -> new Bishop(color, col, row);
        case 'q' -> new Queen(color, col, row);
        case 'k' -> new King(color, col, row);
        default -> null;
    };
}
protected String getSaveGameModeTag() {
    return "STANDARD";
}


}
