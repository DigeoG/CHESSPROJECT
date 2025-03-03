package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;

public class Piece {

    // Core attributes for a chess piece
    public Type type;
    public BufferedImage image;
    public int x, y;           // Pixel coordinates on the board
    public int col, row;        // Current board position
    public int preCol, preRow;  // Previous position (for move tracking)
    public int color;
    public Piece hittingP;      // Piece this one is about to capture (if any)
    public boolean moved;       // True after first move
    public boolean twoStepped;  // For pawns moving two squares initially

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        updatePixelCoordinates();

        preCol = col;
        preRow = row;
    }

    // Load piece image based on provided path
    public BufferedImage getImage(String imagePath) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Convert board column to x-coordinate
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    // Convert board row to y-coordinate
    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    // Convert x-coordinate to board column
    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    // Convert y-coordinate to board row
    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    // Find the index of this piece in the global piece list
    public int getIndex() {
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            if (GamePanel.simPieces.get(i) == this) {
                return i;
            }
        }
        return -1;  // Return -1 if not found (should never happen in normal play)
    }

    // Called after a successful move to update coordinates and state
    public void updatePosition() {
        if (type == Type.PAWN && Math.abs(row - preRow) == 2) {
            twoStepped = true;
        }
        updatePixelCoordinates();
        preCol = col;
        preRow = row;
        moved = true;
    }

    // Return piece to its last known position (used for illegal move reversals)
    public void resetPosition() {
        col = preCol;
        row = preRow;
        updatePixelCoordinates();
    }

    // Base movement check (overridden by specific pieces)
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    // Check if a square is within the bounds of the 8x8 board
    public boolean isWithinBoard(int targetCol, int targetRow) {
        return targetCol >= 0 && targetCol < 8 && targetRow >= 0 && targetRow < 8;
    }

    // Check if the target square is the same square the piece is already on
    public boolean isSameSquare(int targetCol, int targetRow) {
        return targetCol == preCol && targetRow == preRow;
    }

    // Find if there’s a piece at the target square (other than self)
    public Piece getHittingP(int targetCol, int targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    // Is the target square empty or occupied by an enemy?
    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) {
            return true;
        }
        if (hittingP.color != this.color) {
            return true;
        }
        hittingP = null;  // Same-color piece blocks the move
        return false;
    }

    // Check if a piece blocks the straight path (Rook/Queen logic)
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        if (targetCol != preCol) {
            // Horizontal movement
            for (int c = Math.min(preCol, targetCol) + 1; c < Math.max(preCol, targetCol); c++) {
                if (isPieceAt(c, preRow)) return true;
            }
        } else {
            // Vertical movement
            for (int r = Math.min(preRow, targetRow) + 1; r < Math.max(preRow, targetRow); r++) {
                if (isPieceAt(preCol, r)) return true;
            }
        }
        return false;
    }

    // Check if a piece blocks the diagonal path (Bishop/Queen logic)
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
        int colStep = (targetCol > preCol) ? 1 : -1;
        int rowStep = (targetRow > preRow) ? 1 : -1;

        for (int c = preCol + colStep, r = preRow + rowStep; c != targetCol; c += colStep, r += rowStep) {
            if (isPieceAt(c, r)) return true;
        }
        return false;
    }

    // Helper function to check if any piece exists at a given square
    private boolean isPieceAt(int col, int row) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == col && piece.row == row) {
                hittingP = piece;
                return true;
            }
        }
        return false;
    }

    // Draw the piece image at its current position
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }

    // Convenience method to update x/y based on col/row
    private void updatePixelCoordinates() {
        x = getX(col);
        y = getY(row);
    }
}
