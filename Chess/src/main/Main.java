package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
    static ImageIcon logo = new ImageIcon(Main.class.getClassLoader().getResource("res/chess.png"));

    public static void main(String[] args) {
        JFrame window = new JFrame("Custom Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setIconImage(logo.getImage());

        String[] options = { "Player vs Player", "Play Against AI", "Zombie Mode" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose Game Mode",
                "Chess",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        GamePanel gp;

        // ✅ Route to correct game mode
        if (choice == 2) {
            System.out.println("🧟 Zombie Mode selected");
            gp = new BattlePromotionGamePanel();//ZombieGamePanel(); // <-- make sure this class exists
        } else {
            boolean againstAI = (choice == 1);
            System.out.println("againstAI = " + againstAI);
            gp = new GamePanel(againstAI);
        }

        window.add(gp);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gp.launchGame();
    }
}
