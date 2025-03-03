package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class ChessGame {
    private final JFrame frame;
    private final LinkedList<Piece> pieces = new LinkedList<>();
    private final Image[] images = new Image[12];

    public ChessGame() {
        frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(512, 512);
        frame.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Game");

        JMenuItem startGame = new JMenuItem("Start Game");
        JMenuItem endGame = new JMenuItem("End Game");
        JMenuItem saveGame = new JMenuItem("Save Game");

        startGame.addActionListener(e -> startNewGame());
        endGame.addActionListener(e -> endCurrentGame());
        saveGame.addActionListener(e -> saveCurrentGame());

        gameMenu.add(startGame);
        gameMenu.add(endGame);
        gameMenu.add(saveGame);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);

        loadPieceImages();
        initializePieces();

        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                renderBoard(g);
                renderPieces(g);
            }
        };

        frame.add(boardPanel);
        frame.setVisible(true);
    }

    private void startNewGame() {
        JOptionPane.showMessageDialog(frame, "Starting a new game!");
        SwingUtilities.invokeLater(() -> {
            GamePanel gamePanel = new GamePanel();
            JFrame gameWindow = new JFrame("Game Panel");
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.add(gamePanel);
            gameWindow.pack();
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);
            gamePanel.launchGame();
        });
        frame.dispose();
    }

    private void endCurrentGame() {
        JOptionPane.showMessageDialog(frame, "Game Over.");
    }

    private void saveCurrentGame() {
        JOptionPane.showMessageDialog(frame, "Game Saved!");
    }

    private void loadPieceImages() {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(System.getProperty("user.home") + "/Downloads/chess.png"));
            int index = 0;
            for (int y = 0; y < 400; y += 200) {
                for (int x = 0; x < 1200; x += 200) {
                    images[index] = spriteSheet.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    index++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializePieces() {
        String[] pieceOrder = {"rook", "knight", "bishop", "queen", "king", "bishop", "knight", "rook"};

        for (int i = 0; i < 8; i++) {
            pieces.add(new Piece(i, 7, true, pieceOrder[i], pieces));
            pieces.add(new Piece(i, 6, true, "pawn", pieces));
            pieces.add(new Piece(i, 0, false, pieceOrder[i], pieces));
            pieces.add(new Piece(i, 1, false, "pawn", pieces));
        }
    }

    private void renderBoard(Graphics g) {
        boolean isWhite = true;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                g.setColor(isWhite ? new Color(235, 235, 208) : new Color(119, 148, 85));
                g.fillRect(col * 64, row * 64, 64, 64);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

    private void renderPieces(Graphics g) {
        HashMap<String, Integer> pieceMap = new HashMap<>();
        pieceMap.put("king", 0);
        pieceMap.put("queen", 1);
        pieceMap.put("bishop", 2);
        pieceMap.put("knight", 3);
        pieceMap.put("rook", 4);
        pieceMap.put("pawn", 5);

        for (Piece piece : pieces) {
            int index = pieceMap.getOrDefault(piece.name.toLowerCase(), -1);
            if (!piece.isWhite) index += 6;
            if (index >= 0) g.drawImage(images[index], piece.xp * 64, piece.yp * 64, null);
        }
    }

    void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
}
