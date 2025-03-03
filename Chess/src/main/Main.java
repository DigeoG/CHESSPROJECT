package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        // Load application logo
        ImageIcon logo = new ImageIcon(Main.class.getClassLoader().getResource("res/chess.png"));

        // Create and configure main application window
        JFrame window = new JFrame("Simple Chess");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setIconImage(logo.getImage());

        // Initialize the GUI
        new perhapsthegui();

        
        
    }
}
