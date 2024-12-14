import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {
    JFrame window;
    GamePanel gamePanel; // game panel
    JLabel points;

    static int width = 800;
    static int height = 800;

    public Game() {
        window = new JFrame("Flappy Bird");
        window.setSize(width, height);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);

        gamePanel = new GamePanel(this);

        // points count
        points = new JLabel();
        points.setFont(new Font("Arial", Font.BOLD, 48));
        points.setText("0");
        points.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(points);

        window.add(gamePanel);

        window.setVisible(true);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}