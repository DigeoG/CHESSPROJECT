package piece;

import java.awt.image.BufferedImage;
import main.GamePanel;
import main.Type;

public class Knight extends Piece {

    // Constructor - Initializes knight with color and position
    public Knight(int color, int col, int row) {
        super(color, col, row);
        type = Type.KNIGHT;  // Set the piece type to Knight
        image = loadKnightImage(color);  // Load the correct image based on color
    }

    // Load the knight's image based on whether it's white or black
    private BufferedImage loadKnightImage(int color) {
        String basePath = "../res/piece/";
        String imageFile = (color == GamePanel.WHITE) ? "w-knight" : "b-knight";
        return getImage(basePath + imageFile);
    }

    // Knight's unique movement logic - it moves in an "L" shape
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        // Calculate the horizontal and vertical distance between current and target square
        int columnDistance = Math.abs(targetCol - preCol);
        int rowDistance = Math.abs(targetRow - preRow);

        // Valid knight moves: 2 squares in one direction, 1 square in the other
        boolean correctMovePattern = 
                (columnDistance == 2 && rowDistance == 1) || 
                (columnDistance == 1 && rowDistance == 2);

        // Move is legal if it follows the knight's movement pattern and the square is open/attackable
        return correctMovePattern && isValidSquare(targetCol, targetRow);
    }
}
