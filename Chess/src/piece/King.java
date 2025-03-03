package piece;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.Type;

public class King extends Piece {

    // Constructor - Initialize king with color and position
    public King(int color, int col, int row) {
        super(color, col, row);
        type = Type.KING;  // Set piece type to king
        image = loadKingImage(color);  // Assign the correct image based on color
    }

    // Load the king's image depending on its color
    private BufferedImage loadKingImage(int color) {
        String imagePath = "../res/piece/";
        return getImage(imagePath + (color == GamePanel.WHITE ? "w-king" : "b-king"));
    }

    // Override the canMove method to define king-specific movement rules
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Ensure target is on the board
        if (!isWithinBoard(targetCol, targetRow)) {
            return false;
        }

        // Kings normally move one square in any direction
        int colDistance = Math.abs(targetCol - preCol);
        int rowDistance = Math.abs(targetRow - preRow);
        boolean isNormalMove = (colDistance + rowDistance == 1) || (colDistance * rowDistance == 1);

        if (isNormalMove && isValidSquare(targetCol, targetRow)) {
            return true;
        }

        // Special case - Castling (only allowed if the king has not yet moved)
        if (!moved) {
            if (checkRightCastling(targetCol, targetRow) || checkLeftCastling(targetCol, targetRow)) {
                return true;
            }
        }

        return false;
    }

    // Check if castling to the right (kingside) is legal
    private boolean checkRightCastling(int targetCol, int targetRow) {
        // Kingside castling moves king two squares right
        if (targetCol != preCol + 2 || targetRow != preRow) {
            return false;
        }
        // Path between king and rook must be clear
        if (pieceIsOnStraightLine(targetCol, targetRow)) {
            return false;
        }
        // Locate the rook on the same row, 3 columns to the right
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == preCol + 3 && piece.row == preRow && !piece.moved) {
                GamePanel.castlingP = piece;  // Set castling rook
                return true;
            }
        }
        return false;
    }

    // Check if castling to the left (queenside) is legal
    private boolean checkLeftCastling(int targetCol, int targetRow) {
        // Queenside castling moves king two squares left
        if (targetCol != preCol - 2 || targetRow != preRow) {
            return false;
        }
        // Path between king and rook must be clear
        if (pieceIsOnStraightLine(targetCol, targetRow)) {
            return false;
        }

        // Check two squares: one must be empty, and one must hold an unmoved rook
        Piece[] castlingSquares = new Piece[2];
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == preCol - 3 && piece.row == targetRow) {
                castlingSquares[0] = piece;  // Square between king and rook
            }
            if (piece.col == preCol - 4 && piece.row == targetRow) {
                castlingSquares[1] = piece;  // The actual rook
            }
        }

        // Castling is legal if inner square is empty and rook is unmoved
        if (castlingSquares[0] == null && castlingSquares[1] != null && !castlingSquares[1].moved) {
            GamePanel.castlingP = castlingSquares[1];  // Set castling rook
            return true;
        }

        return false;
    }
}
