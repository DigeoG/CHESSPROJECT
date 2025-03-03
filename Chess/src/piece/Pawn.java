package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

    // Constructor - Initializes the pawn with color and position
    public Pawn(int color, int col, int row) {
        super(color, col, row);
        type = Type.PAWN;  // Identify this piece as a pawn

        // Load correct pawn image based on color
        if (color == GamePanel.WHITE) {
            image = getImage("/res/piece/w-pawn");
        } else {
            image = getImage("/res/piece/b-pawn");
        }
    }

    // Override to define pawn-specific movement rules
    @Override
    public boolean canMove(int targetCol, int targetRow) {
        if (!isWithinBoard(targetCol, targetRow) || isSameSquare(targetCol, targetRow)) {
            return false;  // Out of bounds or trying to stay put
        }

        // Movement direction depends on color (white moves up, black moves down)
        int direction = (color == GamePanel.WHITE) ? -1 : 1;

        // Determine if a piece is on the target square
        hittingP = getHittingP(targetCol, targetRow);

        // 1-square forward move (only allowed if square is empty)
        if (targetCol == preCol && targetRow == preRow + direction && hittingP == null) {
            return true;
        }

        // 2-square initial move (only allowed if pawn hasn't moved and no obstacles)
        if (targetCol == preCol && targetRow == preRow + 2 * direction 
                && !moved && hittingP == null && !pieceIsOnStraightLine(targetCol, targetRow)) {
            return true;
        }

        // Diagonal capture - pawn captures opponent diagonally
        if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + direction 
                && hittingP != null && hittingP.color != color) {
            return true;
        }

        // En Passant capture (special pawn rule)
        if (Math.abs(targetCol - preCol) == 1 && targetRow == preRow + direction) {
            for (Piece piece : GamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == preRow && piece.twoStepped) {
                    hittingP = piece;  // Capture the pawn that just did a two-step move
                    return true;
                }
            }
        }

        // If none of the valid pawn moves apply, return false
        return false;
    }
}
