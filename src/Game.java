public class Game {
    GameWindow window;
    GamePanel panel;

    public Game() {
        panel = new GamePanel();
        window = new GameWindow(panel);
    }
}
