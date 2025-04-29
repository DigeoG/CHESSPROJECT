package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.SwingUtilities;
import main.Board;
import static main.GamePanel.BLACK;
import static main.GamePanel.WHITE;
import static main.GamePanel.simPieces;
import piece.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;



public class BattlePromotionGamePanel extends GamePanel {

    private Piece capturedTarget = null;
    private int whiteMoveCounter = 0;

    public BattlePromotionGamePanel() {
        super(false); // No AI
        
        
     JButton instructionsButton = new JButton("📖 Instructions");
    instructionsButton.setBounds(8 * Board.SQUARE_SIZE + 20, 10, 150, 30); // Position at right side
    instructionsButton.addActionListener(e -> showInstructions());
    setLayout(null);  // Allow absolute positioning
    add(instructionsButton);
    }

    @Override
    public boolean canPromote() {
        if (activeP != null && activeP.type == Type.PAWN) {
            if ((currentColor == WHITE && activeP.row == 0) ||
                (currentColor == BLACK && activeP.row == 7)) {
                prepareBattlePromotion(currentColor);
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if (promotion) {
            promoting();
            return;
        }

        if (gameover || stalemate) return;

        if (mouse.pressed) {
            if (activeP == null) {
                for (Piece piece : simPieces) {
                    if (piece.color == currentColor &&
                        piece.col == mouse.x / Board.SQUARE_SIZE &&
                        piece.row == mouse.y / Board.SQUARE_SIZE) {
                        activeP = piece;
                        capturedTarget = null;
                        break;
                    }
                }
            } else {
                simulate();

                int toCol = mouse.x / Board.SQUARE_SIZE;
                int toRow = mouse.y / Board.SQUARE_SIZE;
                Piece maybeCaptured = getPieceAt(toCol, toRow);

                capturedTarget = (maybeCaptured != null && maybeCaptured.color != currentColor)
                                 ? maybeCaptured
                                 : null;
            }
        }

        if (!mouse.pressed && activeP != null) {
            int toCol = mouse.x / Board.SQUARE_SIZE;
            int toRow = mouse.y / Board.SQUARE_SIZE;

            if (validSquare) {
                boolean captured = (capturedTarget != null);

                copyPieces(simPieces, pieces);
                activeP.col = toCol;
                activeP.row = toRow;
                activeP.updatePosition();

                if (castlingP != null) {
                    castlingP.updatePosition();
                }

                if (isKingInCheck() && isCheckMate()) {
                    gameover = true;
                } else if (isStalemate() && !isKingInCheck()) {
                    stalemate = true;
                } 
                else if (captured) {
                    prepareBattlePromotion(currentColor);
                    promotion = true;
                    promotingPawn = activeP;
                    capturedTarget = null;
                    return;
                }
                else if (canPromote()) {
                    promotion = true;
                    promotingPawn = activeP;
                    capturedTarget = null;
                    return;
                } 
                else {
                    changePlayer();
                }
            } else {
                System.out.println("Invalid move at: col=" + toCol + ", row=" + toRow);
                copyPieces(pieces, simPieces);
                activeP.resetPosition();
            }

            activeP = null;
            capturedTarget = null;
        }
    }

@Override
public void promoting() {
    if (promoPieces.isEmpty()) {
        promotion = false;
        return;
    }

    if (mouse.pressed) {
        for (Piece piece : promoPieces) {
            if (piece.col == mouse.x / Board.SQUARE_SIZE &&
                piece.row == mouse.y / Board.SQUARE_SIZE) {

                if (promotingPawn != null) {
                    // 🛡 Normal battle promotion after capture
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
                    promotingPawn = null;
                    promotion = false;
                    changePlayer();
                    return;
                } else {
                    // 🎯 Bonus White Promotion - try (0,2), if occupied search next
                    int promoteCol = 0;
                    int promoteRow = 2;

                    if (getPieceAt(promoteCol, promoteRow) != null) {
                        // Search for next available empty square
                        boolean found = false;
                        for (int row = 0; row < 8 && !found; row++) {
                            for (int col = 0; col < 8 && !found; col++) {
                                if (getPieceAt(col, row) == null) {
                                    promoteCol = col;
                                    promoteRow = row;
                                    found = true;
                                }
                            }
                        }

                        if (!found) {
                            System.out.println("⚠️ No empty square found for bonus promotion!");
                            promotion = false;
                            promotionStage = 0;
                            selectedPromotionType = null;
                            return;
                        }
                    }

                    // Place selected piece at found promoteCol, promoteRow
                    switch (piece.type) {
                        case ROOK:
                            simPieces.add(new Rook(WHITE, promoteCol, promoteRow));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(WHITE, promoteCol, promoteRow));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(WHITE, promoteCol, promoteRow));
                            break;
                        case QUEEN:
                            simPieces.add(new Queen(WHITE, promoteCol, promoteRow));
                            break;
                    }
                    
                    // 🛡️ Insert flash-start code immediately after adding the new piece:
                    lastPromotedCol = promoteCol;
                    lastPromotedRow = promoteRow;
                    flashStartTime = System.currentTimeMillis();  // Start flashing now
                    flashing = true;

                    copyPieces(simPieces, pieces);

                    activeP = null;
                    promotingPawn = null;
                    promotion = false;
                    promotionStage = 0;
                    selectedPromotionType = null;
                    changePlayer();
                    return;
                }
            }
        }
    }
}


    private void prepareBattlePromotion(int color) {
        promoPieces.clear();
        int promoCol = 8;
        int promoStartRow = 2;

        promoPieces.add(new Rook(color, promoCol, promoStartRow));
        promoPieces.add(new Knight(color, promoCol, promoStartRow + 1));
        promoPieces.add(new Bishop(color, promoCol, promoStartRow + 2));
        promoPieces.add(new Queen(color, promoCol, promoStartRow + 3));
    }

    @Override
    public void simulate() {
        canMove = false;
        validSquare = false;

        copyPieces(pieces, simPieces);

        if (castlingP != null) {
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if (activeP.canMove(activeP.col, activeP.row)) {
            canMove = true;

            if (activeP.hittingP != null) {
                if (activeP.hittingP.color != currentColor) {
                    capturedTarget = activeP.hittingP;
                }
                simPieces.remove(activeP.hittingP.getIndex());
            } else {
                capturedTarget = null;
            }

            checkCastling();

            if (!isIllegal(activeP) && !opponentCanCaptureKing()) {
                validSquare = true;
            }
        } else {
            capturedTarget = null;
        }

        repaint();
    }

    @Override
    public void changePlayer() {
        if (currentColor == WHITE) {
            whiteMoveCounter++;

            if (whiteMoveCounter % 7 == 0) {
                prepareBattlePromotion(WHITE);
                promotion = true;
                promotingPawn = null;
                promotionStage = 0;
                selectedPromotionType = null;
                return;
            }
        }

        currentColor = (currentColor == WHITE) ? BLACK : WHITE;
        aiTurnPending = (againstAI && currentColor == BLACK);
    }

    // 🔵 New fields for bonus promotion
    private int promotionStage = 0; // 0 = choosing piece, 1 = choosing empty square
    private Type selectedPromotionType = null;

// Bonus Promotion Variables

private int lastPromotedCol = -1;
private int lastPromotedRow = -1;
private long flashStartTime = 0;
private boolean flashing = false;


    
@Override
public void paintComponent(Graphics g) {
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
    
        if (flashing) {
        long elapsed = System.currentTimeMillis() - flashStartTime;
        if (elapsed < 1000) { // Flash for 1 second
            g.setColor(Color.YELLOW);
            g.fillRect(lastPromotedCol * Board.SQUARE_SIZE, lastPromotedRow * Board.SQUARE_SIZE, Board.SQUARE_SIZE, Board.SQUARE_SIZE);
        } else {
            flashing = false; // Stop flashing after 1 second
        }
    }

    // ✨ Draw White Bonus Promotion Counter
    if (currentColor == WHITE) {
        int movesLeft = 7 - (whiteMoveCounter % 7);
        if (movesLeft != 7) { // Not exactly after reset
            g.setColor(Color.BLUE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            //g.drawString("White Bonus Promotion in " + movesLeft + " Moves!", 20, 30);
             g.drawString(
                "Bonus in " + movesLeft + " moves",
                8 * Board.SQUARE_SIZE + 10, // X coordinate: next to board
                Board.SQUARE_SIZE           // Y coordinate: above promo options
            );
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

private void showInstructions() {
    String instructions = """
🏰 Battle Promotion Chess — How to Play

⚔️ 1. Battle Promotions:
- Capture a piece = immediately promote your piece (rook, knight, bishop, queen).

⚡ 2. White Bonus Promotions:
- After every 7 white moves, white gets a free bonus promotion.
- The bonus piece appears at (0,2) or the next empty square.

📢 3. Visuals:
- Flashing yellow square shows where bonus piece appears.
- "Bonus in X moves" counter shows progress.

🧠 Strategy Tip:
- Capture to upgrade.
- Plan White's bonus power-up attacks!

🎉 Good luck, Commander!
""";

    JOptionPane.showMessageDialog(this, instructions, "📖 How to Play", JOptionPane.INFORMATION_MESSAGE);
}

@Override
protected String getSaveGameModeTag() {
    return "BATTLE"; 
}



}




