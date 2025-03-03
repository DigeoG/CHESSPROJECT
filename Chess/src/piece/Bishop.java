package piece;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.Type;

public class Bishop extends Piece {

    // Constructor - Initializes bishop with its color and position
    public Bishop(int color, int col, int row) {
        super(color, col, row);
        type = Type.BISHOP;  // Set type to bishop
        image = loadBishopImage(color);  // Load appropriate image based on color
    }

    // Load bishop image based on color
    private BufferedImage loadBishopImage(int color) {
        String basePath = "../res/piece/";
        String fileName = (color == GamePanel.WHITE) ? "w-bishop" : "b-bishop";
        return getImage(basePath + fileName);
    }

    // Override movement logic to define how the bishop can move
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Movement must stay within the board, and bishop can't move to its own square
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }

        // Bishop only moves diagonally (row difference must match column difference)
        boolean diagonalMovement = Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow);

        // Ensure no piece is blocking the diagonal path
        boolean clearPath = !pieceIsOnDiagonalLine(targetCol, targetRow);

        // Move is valid if both diagonal movement and clear path conditions are satisfied,
        // and the target square itself is valid (either empty or enemy piece)
        return diagonalMovement && clearPath && isValidSquare(targetCol, targetRow);
    }
}
