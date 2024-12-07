import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {
    JFrame window;
    JPanel cardPanel; // Panel with card layout
    CardLayout cardLayout; // card layout for switching screens
    GamePanel gamePanel; // game panel
    JLabel points;

    public Game() {
        window = new JFrame("Flappy Bird");
        window.setSize(800, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel menuPanel = createMenuPanel(); // TODO

        gamePanel = new GamePanel(this);

        // points count
        points = new JLabel();
        points.setFont(new Font("Arial", Font.BOLD, 48));
        points.setText("0");
        points.setAlignmentX(Component.CENTER_ALIGNMENT);
        gamePanel.add(points);

        cardPanel.add(menuPanel, "Menu");
        cardPanel.add(gamePanel, "Game");

        window.add(cardPanel);

        window.setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
        menuPanel.setBackground(Color.GRAY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding around components
        gbc.gridx = 0; // Center horizontally
        gbc.gridy = 0; // Position the title first
        gbc.anchor = GridBagConstraints.CENTER; // Ensure components are centered

        // Title
        JLabel title = new JLabel("Flappy Bird");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        menuPanel.add(title, gbc);

        // Spacing between title and button
        gbc.gridy++;
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50)), gbc);

        // Play button
        gbc.gridy++;
        JButton startButton = getjButton();
        menuPanel.add(startButton, gbc);

        return menuPanel;
    }


    private JButton getjButton() {
        JButton startButton = new JButton("PLAY");
        startButton.setBackground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "Game"); // Switch to the game panel
                gamePanel.restartGame(); // Restart the game
                gamePanel.requestFocusInWindow(); // Ensure the game panel has focus
            }
        });
        return startButton;
    }

    public void showMenu() {
        cardLayout.show(cardPanel, "Menu"); // Switch back to the menu panel
    }
}
