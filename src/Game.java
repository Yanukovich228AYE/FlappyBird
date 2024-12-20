import javax.swing.*;
import java.awt.*;

public class Game {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private JFrame window;
    GamePanel gamePanel;
    JLabel points;

    public Game() {
        window = new JFrame("Flappy Bird");
        window.setSize(WIDTH, HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);

        // Main game panel
        gamePanel = new GamePanel(this);
        gamePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        // Points label
        points = new JLabel("0", SwingConstants.CENTER);
        points.setFont(new Font("Arial", Font.BOLD, 48));
        points.setForeground(Color.WHITE);
        // Optional: Set a translucent background if desired
        // points.setOpaque(true);
        // points.setBackground(new Color(0,0,0,128));

        // Add points inside the game panel
        gamePanel.add(points);

        // Add the game panel to the window
        window.add(gamePanel);

        window.setVisible(true);
    }

    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}
