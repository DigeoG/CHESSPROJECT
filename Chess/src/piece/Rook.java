package piece;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.Type;

public class Rook extends Piece {

    // Constructor - Sets the rook's color, position, and type
    public Rook(int color, int col, int row) {
        super(color, col, row);
        type = Type.ROOK;  // Identify this piece as a rook
        image = loadRookImage(color);  // Load image based on color
    }

    // Fetch the image for the rook based on its color (white or black)
    private BufferedImage loadRookImage(int color) {
        String basePath = "../res/piece/";
        String fileName = (color == GamePanel.WHITE) ? "w-rook" : "b-rook";
        return getImage(basePath + fileName);
    }

    // Override the base canMove method to implement rook movement rules
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Rook must move to a valid square within the board, and it can't stay in the same square
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }

        // Rook can only move along straight lines (no diagonals)
        boolean movingInStraightLine = (targetCol == preCol || targetRow == preRow);

        // Valid move if square is empty or enemy-occupied AND no pieces block the path
        return movingInStraightLine && isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow);
    }
}
