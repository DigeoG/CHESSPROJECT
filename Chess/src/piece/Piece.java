package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;
import main.Type;


public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;
    public static final int WHITE = 0;
    public static final int BLACK = 1;


    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);

        preCol = col;
        preRow = row;
    }

    public Piece(Piece p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE) / Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for (int index = 0; index < GamePanel.simPieces.size(); index++) {
            if (GamePanel.simPieces.get(index) == this) {
                return index;
            }
        }

        return 0;
    }

public void updatePosition() {
    if (type == Type.PAWN) {
        if (Math.abs(row - preRow) == 2) {
            twoStepped = true;
        }
    }

    preCol = col;  // Store previous position before updating
    preRow = row;
    
    x = getX(col);
    y = getY(row);

    moved = true;
}


    public void resetPosition() {
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }

        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow) {
        if (targetCol == preCol && targetRow == preRow) {
            return true;
        }

        return false;
    }

public Piece getHittingP(int targetCol, int targetRow) {
    for (Piece piece : GamePanel.simPieces) {
        if (piece.col == targetCol && piece.row == targetRow && piece != this) {
            //System.out.println("Piece at (" + targetCol + ", " + targetRow + ") is blocking.");
            return piece;
        }
    }
    return null;
}


    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);

        if (hittingP == null) { // This is a valid square
            return true;
        } else { // This is not a valid square
            if (hittingP.color != this.color) { // If the piece is not the same color
                return true;
            } else {
                hittingP = null;
            }
        }

        return false;
    }

public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
    // Moving left
    for (int c = preCol - 1; c > targetCol; c--) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == c && piece.row == preRow) {
                hittingP = piece;
                return true;
            }
        }
    }

    // Moving right
    for (int c = preCol + 1; c < targetCol; c++) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == c && piece.row == preRow) {
                hittingP = piece;
                return true;
            }
        }
    }

    // Moving up
    for (int r = preRow - 1; r > targetRow; r--) {  // Fixed incorrect loop bound
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == preCol && piece.row == r) {
                hittingP = piece;
                return true;
            }
        }
    }

    // Moving down
    for (int r = preRow + 1; r < targetRow; r++) {  // Fixed incorrect loop bound
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == preCol && piece.row == r) {
                hittingP = piece;
                return true;
            }
        }
    }
    
    return false;
}


public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow) {
    int colStep = (targetCol > preCol) ? 1 : -1;
    int rowStep = (targetRow > preRow) ? 1 : -1;

    int c = preCol + colStep;
    int r = preRow + rowStep;

    while (c != targetCol && r != targetRow) {
        for (Piece piece : GamePanel.simPieces) {
            if (piece.col == c && piece.row == r) {
                hittingP = piece;
                return true;
            }
        }
        c += colStep;
        r += rowStep;
    }

    return false;
}


    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
    
    public String getFENChar() {
    char fenChar;
    
    
    if (this instanceof Pawn) {
        fenChar = 'P';
    } else if (this instanceof Knight) {
        fenChar = 'N';
    } else if (this instanceof Bishop) {
        fenChar = 'B';
    } else if (this instanceof Rook) {
        fenChar = 'R';
    } else if (this instanceof Queen) {
        fenChar = 'Q';
    } else if (this instanceof King) {
        fenChar = 'K';
    } else {
        return "?"; // Unknown piece
    }
    
    //System.out.println("FEN Char for " + this.getClass().getSimpleName() + ": " + fenChar);

    // Convert to lowercase if the piece is black
    return (this.color == BLACK) ? String.valueOf(Character.toLowerCase(fenChar)) : String.valueOf(fenChar);
    
}
    
    public void changeToColor(int newColor) {
    this.color = newColor;

    // Reload the correct image for the new color and piece type
    String path = "/pieces/";
    switch (this.type) {
        case PAWN:
            path += (newColor == WHITE) ? "wp" : "bp";
            break;
        case ROOK:
            path += (newColor == WHITE) ? "wr" : "br";
            break;
        case KNIGHT:
            path += (newColor == WHITE) ? "wn" : "bn";
            break;
        case BISHOP:
            path += (newColor == WHITE) ? "wb" : "bb";
            break;
        case QUEEN:
            path += (newColor == WHITE) ? "wq" : "bq";
            break;
        case KING:
            path += (newColor == WHITE) ? "wk" : "bk";
            break;
    }
    this.image = getImage(path); // Reload new correct image
}


}




//some rules dont work 
//e.g black pawns cant move double step
// white left rook cant move, 
// white queen acts as rook etc

