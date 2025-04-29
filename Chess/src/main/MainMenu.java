package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainMenu extends JFrame {

    private final ImageIcon logo = new ImageIcon(Main.class.getClassLoader().getResource("res/chess.png"));

    public MainMenu() {
        setTitle("Custom Chess - Main Menu");
        setSize(300, 300);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(logo.getImage());
        setLayout(new GridLayout(5, 1, 10, 10));

        JButton loadButton = new JButton("▶ Load Game");
        JButton optionsButton = new JButton("🎮 Game Options");
        JButton instructionsButton = new JButton("📖 Instructions");
        JButton exitButton = new JButton("❌ Exit");

        loadButton.addActionListener(this::loadGame);
        optionsButton.addActionListener(this::openGameOptions);
        instructionsButton.addActionListener(this::showInstructions);
        exitButton.addActionListener(e -> System.exit(0));

        add(Box.createVerticalStrut(10)); // padding
        add(loadButton);
        add(optionsButton);
        add(instructionsButton);
        add(exitButton);
    }

    private void loadGame(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Saved Game");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String gameModeTag = reader.readLine();  // First line = mode
                String fen = reader.readLine();

                GamePanel gp;

                // Create correct game panel based on saved mode
                switch (gameModeTag) {
                    case "STANDARD" -> gp = new GamePanel(false); // Player vs Player
                    case "AI" -> gp = new GamePanel(true);        // Player vs AI
                    case "BATTLE" -> gp = new BattlePromotionGamePanel();
                      // (if you have it)
                    default -> {
                        JOptionPane.showMessageDialog(this, "⚠️ Unknown saved game mode: " + gameModeTag);
                        return;
                    }
                }

                // Set up new game window
                JFrame gameWindow = new JFrame("Custom Chess");
                gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameWindow.setResizable(false);
                gameWindow.setIconImage(logo.getImage());
                gameWindow.add(gp);
                gameWindow.pack();
                gameWindow.setLocationRelativeTo(null);
                gameWindow.setJMenuBar(createMenuBar(gameWindow, gp)); // Create menu bar for loaded game
                gameWindow.setVisible(true);

                // Load the actual FEN into the panel
                gp.loadFromFEN(fen);
                gp.launchGame();

                dispose(); // Close Main Menu
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "⚠️ Failed to load saved game: " + ex.getMessage());
            }
        }
    }

    private void openGameOptions(ActionEvent e) {
        String[] options = { "Player vs Player", "Play Against AI", "Battle Promotion Mode" };
        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose Game Mode",
                "Game Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) return;

        GamePanel gp;
        if (choice == 2) {
            gp = new BattlePromotionGamePanel(); // make sure you have this
        } else {
            boolean againstAI = (choice == 1);
            gp = new GamePanel(againstAI);
        }

        JFrame gameWindow = new JFrame("Custom Chess");
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setResizable(false);
        gameWindow.setIconImage(logo.getImage());
        gameWindow.add(gp);
        gameWindow.pack();
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setJMenuBar(createMenuBar(gameWindow, gp));
        gameWindow.setVisible(true);

        gp.launchGame();
        dispose(); // close main menu
    }

    private void showInstructions(ActionEvent e) {
        String instructions = """
♟️ Basic Chess Instructions

🎯 Objective:
- Checkmate your opponent's King — meaning their King is under threat of capture and cannot escape.

♟️ Piece Movement:
- Pawns: Move forward 1 square (first move can be 2 squares), capture diagonally.
- Rooks: Move any number of squares vertically or horizontally.
- Knights: Move in an 'L' shape (2 squares + 1 square).
- Bishops: Move any number of squares diagonally.
- Queens: Move any number of squares in any direction.
- Kings: Move 1 square in any direction.

🔄 Special Moves:
- Castling, En Passant, Pawn Promotion.

🕹️ Controls:
- Drag and drop to move your pieces.

🏆 Goal:
- Protect your King while attacking your opponent's King!
""";
        JOptionPane.showMessageDialog(this, instructions, "📖 How to Play", JOptionPane.INFORMATION_MESSAGE);
    }

    private JMenuBar createMenuBar(JFrame parentWindow, GamePanel gp) {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu gameMenu = new JMenu("Game");

        JMenuItem returnToMenu = new JMenuItem("Return to Main Menu");
        JMenuItem saveGame = new JMenuItem("Save Game");
        JMenuItem loadGame = new JMenuItem("Load Game");
        JMenuItem exitGame = new JMenuItem("Exit Game");

        returnToMenu.addActionListener(e -> {
            parentWindow.dispose();
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });

        saveGame.addActionListener(e -> {
            gp.saveGame();
        });

        loadGame.addActionListener(e -> {
            gp.loadGame();
        });

        exitGame.addActionListener(e -> System.exit(0));

        gameMenu.add(returnToMenu);
        gameMenu.addSeparator();
        gameMenu.add(saveGame);
        gameMenu.add(loadGame);
        gameMenu.addSeparator();
        gameMenu.add(exitGame);

        menuBar.add(gameMenu);

        return menuBar;
    }
}
