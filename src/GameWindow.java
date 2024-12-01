import javax.swing.*;

public class GameWindow {
    JFrame f;

    public GameWindow(GamePanel panel) {
        f = new JFrame();

        f.setSize(800, 800);
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.add(panel);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
