import javax.swing.*;
import java.awt.*;

public class Game {
    JFrame window;
    JPanel cardPanel; // Panel with card layout
    CardLayout cardLayout; // card layout for switching screens
    GamePanel gamePanel; // game panel

    public Game() {
        window = new JFrame("Flappy Bird");
        window.setSize(800, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel(); // TODO

        cardPanel.add(menuPanel, "Menu");
        cardPanel.add(gamePanel, "Game");

        window.add(cardPanel);

        window.setVisible(true);
    }

    private JPanel createMenuPanel() {
        return new JPanel();
    }
}
