import javax.swing.*;
import java.awt.*;

public class Game {
    JFrame f;
    GamePanel panel;
    JLabel pointsLabel;

    public final int width = 800;
    public final int height = 800;

    public Game() {
        f = new JFrame();

        f.setSize(width, height);
        f.setLocationRelativeTo(null);
        f.setResizable(false);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new GamePanel(this);
        pointsLabel = new JLabel("0");
        pointsLabel.setFont(new Font("Serif", Font.BOLD, 30));

        panel.add(pointsLabel);

        f.add(panel);
        f.setVisible(true);
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
}
