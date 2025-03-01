/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import java.util.HashMap;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChessGame {
    private JFrame frame;
    private LinkedList<Piece> ps = new LinkedList<>();
    private Image imgs[] = new Image[12];

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

        // Action Listeners
        startGame.addActionListener(e -> startGame());
        endGame.addActionListener(e -> endGame());
        saveGame.addActionListener(e -> saveGame());

        gameMenu.add(startGame);
        gameMenu.add(endGame);
        gameMenu.add(saveGame);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar); 

        try {
            BufferedImage all = ImageIO.read(new File(System.getProperty("user.home") + "/Downloads/chess.png"));
            int ind = 0;
            for (int y = 0; y < 400; y += 200) {
                for (int x = 0; x < 1200; x += 200) {
                    imgs[ind] = all.getSubimage(x, y, 200, 200).getScaledInstance(64, 64, BufferedImage.SCALE_SMOOTH);
                    ind++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        initPieces();

        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
                drawPieces(g);
            }
        };

        frame.add(boardPanel);
        frame.setVisible(true);
    }
private void startGame() {
   JOptionPane.showMessageDialog(frame, "Starting a new game!");
    SwingUtilities.invokeLater(() -> {
        // Create a new GamePanel instance
        GamePanel gp = new GamePanel();
        
        // Create a new window for the game panel
        JFrame window = new JFrame("Game Panel");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(gp); // Add the GamePanel to the window
        window.pack(); // Pack the window to fit the contents (GamePanel)
        window.setLocationRelativeTo(null); // Center the window on screen
        window.setVisible(true); // Make the window visible
        
        // Launch the game
        gp.launchGame();
    });
    
    frame.dispose(); // Close the current ChessGame window
}
    private void endGame() {
        JOptionPane.showMessageDialog(frame, "Game Over.");
        // Implement end game logic
    }

    private void saveGame() {
        JOptionPane.showMessageDialog(frame, "Game Saved!");
        // Implement saving logic
    }

    private void initPieces() {
        // White pieces
        ps.add(new Piece(4, 7, true, "king", ps));
        ps.add(new Piece(3, 7, true, "queen", ps));
        ps.add(new Piece(0, 7, true, "rook", ps));
        ps.add(new Piece(1, 7, true, "knight", ps));
        ps.add(new Piece(2, 7, true, "bishop", ps));
        ps.add(new Piece(5, 7, true, "bishop", ps));
        ps.add(new Piece(6, 7, true, "knight", ps));
        ps.add(new Piece(7, 7, true, "rook", ps));
        for (int i = 0; i < 8; i++) {
            ps.add(new Piece(i, 6, true, "pawn", ps));
        }

        // Black pieces
        ps.add(new Piece(4, 0, false, "king", ps));
        ps.add(new Piece(3, 0, false, "queen", ps));
        ps.add(new Piece(0, 0, false, "rook", ps));
        ps.add(new Piece(1, 0, false, "knight", ps));
        ps.add(new Piece(2, 0, false, "bishop", ps));
        ps.add(new Piece(5, 0, false, "bishop", ps));
        ps.add(new Piece(6, 0, false, "knight", ps));
        ps.add(new Piece(7, 0, false, "rook", ps));
        for (int i = 0; i < 8; i++) {
            ps.add(new Piece(i, 1, false, "pawn", ps));
        }
    }

    private void drawBoard(Graphics g) {
        boolean white = true;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                g.setColor(white ? new Color(235, 235, 208) : new Color(119, 148, 85));
                g.fillRect(x * 64, y * 64, 64, 64);
                white = !white;
            }
            white = !white;
        }
    }



private void drawPieces(Graphics g) {
    HashMap<String, Integer> pieceIndices = new HashMap<>();
    pieceIndices.put("king", 0);
    pieceIndices.put("queen", 1);
    pieceIndices.put("bishop", 2);
    pieceIndices.put("knight", 3);
    pieceIndices.put("rook", 4);
    pieceIndices.put("pawn", 5);

    for (Piece p : ps) {
        int ind = pieceIndices.getOrDefault(p.name.toLowerCase(), -1);
        if (!p.isWhite) ind += 6;
        if (ind >= 0) g.drawImage(imgs[ind], p.xp * 64, p.yp * 64, null);
    }
}
}