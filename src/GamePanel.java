import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.net.URL;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Game game;
    private final Timer timer;
    private int x1, x2; // For animating the background and ground
    private int birdFrame, pipeFrame;
    private int lastSpawnedPipe;
    private long lastAnimationTime, lastPipeSpawnedTime;
    private int pointCount = 0;
    private boolean isRunning = false;

    private Bird bird;
    private final Image[] birdFrames;
    private final Image[] pipeFrames;
    private final Pipe[] pipes;
    private final Image bg1;
    private final Image ground1, ground2;

    // Constants for game mechanics
    private static final int GRAVITY = 1;
    private static final int BIRD_JUMP_VELOCITY = -15;
    private static final int GAME_VELOCITY = 4;
    private static final int BIRD_ANIMATION_INTERVAL = 100;
    private static final int PIPE_SPAWN_INTERVAL = 2600; // in ms, can be adjusted


    public GamePanel(Game game) {
        this.game = game;
        timer = new Timer(16, this); // actionPerformed method will be responsible for updating panel every 20ms
        this.birdFrames = new Image[] {
                loadImg("resources/Bird/birdRedUp.png"),
                loadImg("resources/Bird/birdRedMiddle.png"),
                loadImg("resources/Bird/birdRedDown.png")
        };
        /* uncomment this and comment above to have bird with balls
        this.birdFrames = new Image[] {
                scaleImage(loadImg("resources/Bird/bird.jpeg"), 50, 50),
                scaleImage(loadImg("resources/Bird/bird.jpeg"), 50, 50),
                scaleImage(loadImg("resources/Bird/bird.jpeg"), 50, 50)
        };
        */

        this.pipeFrames = new Image[] {
                loadImg("resources/Pipe/pipeUp.png"),
                loadImg("resources/Pipe/pipeMiddleUp.png"),
                loadImg("resources/Pipe/pipeMiddle.png"),
                loadImg("resources/Pipe/pipeMiddleLow.png"),
                loadImg("resources/Pipe/pipeLow.png")
        };
        int pipeWidth = pipeFrames[0].getWidth(null);
        this.pipes = new Pipe[] {
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 0),
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 1),
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 2),
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 3),
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 4)
        };
        bird = new Bird(game.getWidth()/2, game.getHeight()/2, 50, 50);
        birdFrame = 0;

        this.ground1 = loadImg("resources/Ground/ground.png");
        this.ground2 = loadImg("resources/Ground/ground.png");
        this.bg1 = loadImg("resources/Background/bgLight.png");

        setFocusable(true);
        addKeyListener(this);

        this.x1 = 0;
        this.x2 = game.width;
        this.pipeFrame = 0;
        this.lastSpawnedPipe = 0;
        this.lastAnimationTime = System.currentTimeMillis();
        this.lastPipeSpawnedTime = System.currentTimeMillis();
        timer.start();
    }


    private Image scaleImage(Image original, int width, int height) {
        // Create a buffered image with transparency
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Draw the scaled image
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(original, 0, 0, width, height, null);
        g2d.dispose();
        return scaled;
    }


    private Image loadImg(String path) {
        try {
            URL imageURL = getClass().getResource(path);
            if (imageURL != null)
                return new ImageIcon(imageURL).getImage();
            else
                throw new FileNotFoundException("Image not found: " + path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // still background
        g.drawImage(bg1, 0, 0, getWidth(), getHeight(), null);

        // draw bird
        Rectangle birdRect = new Rectangle(getWidth()/2-birdFrames[birdFrame].getWidth(null)/2, bird.getY(), birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null));
        g.drawImage(birdFrames[birdFrame], getWidth()/2-birdFrames[birdFrame].getWidth(null)/2, bird.getY(), birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null), null);

        for (Pipe pipe : pipes) {
            if (pipe.active) {
                g.drawImage(pipeFrames[pipe.getFrameIndex()], pipe.getX(), pipe.getY1(), pipe.getWidth(), getHeight(), null);
                g.drawRect(pipe.getX(), pipe.getY1(), pipe.getWidth(), pipe.getHeight1());
                g.drawRect(pipe.getX(), pipe.getY2(), pipe.getWidth(), pipe.getHeight2());
                if (birdRect.intersects(pipe.getUpper()) || birdRect.intersects(pipe.getLower())) // check bird intersection with pipe
                    restartGame();
            }
        }

        g.drawImage(ground1, x1, getHeight()-ground1.getHeight(null), game.getWidth(), ground1.getHeight(null), null);
        g.drawImage(ground2, x2, getHeight()-ground2.getHeight(null), game.getWidth(), ground2.getHeight(null), null);
    }

    // method that is responsible for updating the game periodically (updating frames)
    @Override
    public void actionPerformed(ActionEvent e) {
        x1 -= GAME_VELOCITY;
        x2 -= GAME_VELOCITY;

        if (x1 <= -game.getWidth()) // controlling background and ground movement
            x1 = x2 + getWidth();
        if (x2 <= -game.getWidth())
            x2 = x1 + getWidth();

        long currentTime = System.currentTimeMillis();
        if (isRunning) {
            bird.setVelocity(bird.getVelocity()+GRAVITY);
            bird.setY(bird.getY()+bird.getVelocity());

            // updates every active pipe
            for (Pipe pipe : pipes) {
                if (pipe.active) {
                    pipe.moveLeft(GAME_VELOCITY);

                    if (!pipe.pointAwarded && bird.getX() > pipe.getX() + pipe.getWidth()) {
                        pointCount++;
                        game.pointsLabel.setText(""+pointCount);
                        pipe.setPointAwarded(true);
                    }

                    if (pipe.getX() <= -pipe.getWidth()) { // if pipe is out of the screen
                        pipe.setActive(false); // deactivate pipe
                        pipe.setPointAwarded(false);
                        pipe.setX(getWidth()); // reset position
                        System.out.println("Pipe deactivated: " + pipe);
                    }
                }
            }

            // Spawn new random pipe every specified interval of time
            if (currentTime - lastPipeSpawnedTime > (GAME_VELOCITY *1000- GAME_VELOCITY *350)) {
                int randPipe;
                while (true) {
                    randPipe = (int) (Math.random() * pipes.length); // get random pipe
                    if (randPipe != lastSpawnedPipe && !pipes[randPipe].active) { // check if the pipe is already spawned
                        pipes[randPipe].setActive(true);
                        lastSpawnedPipe = randPipe;
                        System.out.println("Pipe activated: " + lastSpawnedPipe);
                        break;
                    }
                }
                lastPipeSpawnedTime = currentTime;
            }

            if (pipes[pipeFrame].getX() <= -pipes[pipeFrame].getWidth()) {
                pipes[pipeFrame].setX(getWidth());
            }

            if (bird.getY() >= getHeight()-birdFrames[birdFrame].getHeight(null)-ground1.getHeight(null)) {
                restartGame();
            }
        }

        if (currentTime - lastAnimationTime > 100) { // changes bird frame every specified interval of time
            if (birdFrame >= 2)
                birdFrame = 0;
            else
                birdFrame++;
            lastAnimationTime = currentTime;
        }

        repaint();
    }

    public void restartGame() {
        // reset bird
        bird.setY(game.getHeight()/2-birdFrames[0].getHeight(null)/2);
        bird.setVelocity(0);

        pointCount = 0;
        game.pointsLabel.setText("0");

        // reset pipes
        for (Pipe pipe : pipes)
            if (pipe.active) {
                pipe.setX(game.getWidth());
                pipe.setActive(false);
            }

        isRunning = false;
    }

    @Override
    public void addNotify() {
        super.addNotify(); // it notifies the game that this is a part of the code for using requestFocus();
        requestFocus(); // requests focus on the game so it's watching for input (keyboard, mouse, etc.)
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { // reacts when specified key is pressed
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!isRunning) {
                isRunning = true;
            }
            bird.setVelocity(BIRD_JUMP_VELOCITY);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
