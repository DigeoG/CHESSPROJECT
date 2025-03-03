package piece;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.Type;

public class Queen extends Piece {

    // Constructor - Initialize the queen with its color and starting position
    public Queen(int color, int col, int row) {
        super(color, col, row);
        type = Type.QUEEN;  // Assign piece type as Queen
        image = loadQueenImage(color);  // Load correct image depending on color
    }

    // Load the appropriate queen image (white or black)
    private BufferedImage loadQueenImage(int color) {
        String basePath = "../res/piece/";
        String imageFile = (color == GamePanel.WHITE) ? "w-queen" : "b-queen";
        return getImage(basePath + imageFile);
    }

    // Define queen movement rules — queen can move like a rook or a bishop
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Ensure target square is on the board and isn't the square the queen already occupies
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;
        }

        // Queen moves horizontally, vertically, or diagonally
        boolean movingStraight = (targetCol == preCol || targetRow == preRow);
        boolean movingDiagonally = (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow));

        // Check straight movement (like a rook)
        if (movingStraight) {
            return isValidSquare(targetCol, targetRow) && !pieceIsOnStraightLine(targetCol, targetRow);
        }

        // Check diagonal movement (like a bishop)
        if (movingDiagonally) {
            return isValidSquare(targetCol, targetRow) && !pieceIsOnDiagonalLine(targetCol, targetRow);
        }

        // If it's neither straight nor diagonal, queen can't move there
        return false;
    }
}
