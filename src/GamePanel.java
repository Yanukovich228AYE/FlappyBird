import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public Bird[] population;
    private final Image[] birdFrames;
    private final Image[] pipeFrames;
    private final Pipe[] pipes;
    private Clip wingClip;
    private final Image bg1;
    private final Image ground1, ground2;

    // Constants for game mechanics
    private static final int GRAVITY = 1;
    private static final int BIRD_JUMP_VELOCITY = -15;
    private static final int GAME_VELOCITY = 4;
    private static final int BIRD_ANIMATION_INTERVAL = 100;
    private static final int PIPE_SPAWN_INTERVAL = 2600; // in ms, can be adjusted
    private static final int POPULATION_SIZE = 20;

    public GamePanel(Game game) {
        this.game = game;
        timer = new Timer(16, this); // actionPerformed method will be responsible for updating panel every 20ms
        this.birdFrames = new Image[] {
                loadImg("resources\\Bird\\birdRedUp.png"),
                loadImg("resources\\Bird\\birdRedMiddle.png"),
                loadImg("resources\\Bird\\birdRedDown.png")
        };
        this.pipeFrames = new Image[] {
                loadImg("resources\\Pipe\\pipeUp.png"),
                loadImg("resources\\Pipe\\pipeMiddleUp.png"),
                loadImg("resources\\Pipe\\pipeMiddle.png"),
                loadImg("resources\\Pipe\\pipeMiddleLow.png"),
                loadImg("resources\\Pipe\\pipeLow.png")
        };
        int pipeWidth = pipeFrames[0].getWidth(null);
        this.pipes = new Pipe[] {
                new Pipe(game.getWidth(), pipeWidth, game.getHeight(), 0),
                new Pipe(game.getWidth(), pipeWidth,game.getHeight(), 1),
                new Pipe(game.getWidth(), pipeWidth,game.getHeight(), 2),
                new Pipe(game.getWidth(), pipeWidth,game.getHeight(), 3),
                new Pipe(game.getWidth(), pipeWidth,game.getHeight(), 4)
        };
        this.bird = new Bird(
                game.getHeight(),
                birdFrames[0].getWidth(null),
                birdFrames[0].getHeight(null),
                game.getHeight()/2 - birdFrames[0].getHeight(null)/2,
                game.getWidth()/2 - birdFrames[0].getWidth(null)/2
        );
        this.population = new Bird[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population[i] = new Bird(
                    game.getHeight(),
                    birdFrames[0].getWidth(null),
                    birdFrames[0].getHeight(null),
                    game.getHeight()/2 - birdFrames[0].getHeight(null)/2,
                    game.getWidth()/2 - birdFrames[0].getWidth(null)/2
            );
        }

        this.wingClip = loadSound("src/resources/Audio/sfx_wing.wav");
        this.ground1 = loadImg("resources/Ground/ground.png");
        this.ground2 = loadImg("resources/Ground/ground.png");
        this.bg1 = loadImg("resources/Background/bgLight.png");

        setFocusable(true);
        addKeyListener(this);

        this.x1 = 0;
        this.x2 = game.getWidth();
        this.bird.setY(game.getHeight()/2-birdFrames[0].getHeight(null)/2);
        this.birdFrame = 0;
        this.pipeFrame = 0;

        this.lastSpawnedPipe = 0;
        this.lastAnimationTime = System.currentTimeMillis();
        this.lastPipeSpawnedTime = System.currentTimeMillis();
        timer.start();
    }

    // TODO
    private Pipe getClosestPipe(int birdX) {
        Pipe closestPipe = null;
        int closestX = Integer.MAX_VALUE;
        for (Pipe pipe : pipes) {
            if (pipe.active && pipe.getX()+pipe.getWidth() > birdX && pipe.getX() - birdX < closestX) {
                closestX = pipe.getX();
                closestPipe = pipe;
            }
        }
        return closestPipe;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(bg1, 0, 0, getWidth(), getHeight(), null);

        // Bird
        Rectangle birdRect = new Rectangle(bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());
        g.drawImage(birdFrames[birdFrame], bird.getX(), bird.getY(), null);

        // Pipes
        for (Pipe pipe : pipes) {
            if (pipe.active) {
                g.drawImage(pipeFrames[pipe.getFrameIndex()], pipe.getX(), pipe.getY1(), pipe.getWidth(), getHeight(), null);

                // Collision check
                if (birdRect.intersects(pipe.getUpper()) || birdRect.intersects(pipe.getLower())) {
                    isRunning = false;
                    timer.stop();
                }

                // Scoring: If bird passes the pipe
                if (!pipe.pointAwarded && bird.getX() > pipe.getX() + pipe.getWidth()) {
                    game.points.setText(String.valueOf(++pointCount));
                    pipe.pointAwarded = true;
                }
            }
        }

        // Ground
        g.drawImage(ground1, x1, getHeight()-ground1.getHeight(null), getWidth(), ground1.getHeight(null), null);
        g.drawImage(ground2, x2, getHeight()-ground2.getHeight(null), getWidth(), ground2.getHeight(null), null);
    }

    // method that is responsible for updating the game periodically (updating frames)
    @Override
    public void actionPerformed(ActionEvent e) {
        x1 -= GAME_VELOCITY;
        x2 -= GAME_VELOCITY;

        if (x1 <= -getWidth())
            x1 = x2 + getWidth();
        if (x2 <= -getWidth())
            x2 = x1 + getWidth();

        if (isRunning) {
            bird.setVelocity(bird.getVelocity() + GRAVITY);
            bird.setY(bird.getY() + bird.getVelocity());

            Pipe closestPipe = getClosestPipe(bird.getX());
            if (closestPipe != null) {
                double birdY = bird.getY() / (double) getHeight();
                double birdVel = bird.getVelocity() / 20.0;
                double closestPipeDist = (closestPipe.getX() - bird.getX()) / (double) getWidth();
                double gapCenter = ((closestPipe.getY2() - closestPipe.getHeight1()) / 2.0) + (double) closestPipe.getHeight1();
                double pipeGapY = gapCenter / (double) getHeight();

                boolean flap = bird.shouldFlap(birdY, birdVel, closestPipeDist, pipeGapY);
                if (flap) {
                    bird.setVelocity(BIRD_JUMP_VELOCITY);
                }
            }

            // Move pipes
            for (Pipe pipe : pipes) {
                if (pipe.active) {
                    pipe.moveLeft(GAME_VELOCITY);
                    if (pipe.getX() <= -pipe.getWidth()) {
                        pipe.setActive(false);
                        pipe.pointAwarded = false;
                        pipe.setX(getWidth());
                    }
                }
            }

            long currentTime = System.currentTimeMillis();
            // Bird animation frame change
            if (currentTime - lastAnimationTime > BIRD_ANIMATION_INTERVAL) {
                birdFrame = (birdFrame + 1) % birdFrames.length;
                lastAnimationTime = currentTime;
            }

            // Spawn new pipes
            if (currentTime - lastPipeSpawnedTime > PIPE_SPAWN_INTERVAL) {
                spawnRandomPipe();
                lastPipeSpawnedTime = currentTime;
            }

            // Check floor collision
            if (bird.getY() >= getHeight() - birdFrames[birdFrame].getHeight(null) - ground1.getHeight(null)) {
                bird.setY(getHeight() - birdFrames[birdFrame].getHeight(null) - ground1.getHeight(null));
                bird.setVelocity(0);
                isRunning = false;
                timer.stop();
            }

            // Prevent going above the top
            if (bird.getY() <= 0) {
                bird.setY(0);
                bird.setVelocity(0);
            }
        }

        repaint();
    }

    private void spawnRandomPipe() {
        while (true) {
            int randPipe = (int) (Math.random() * pipes.length);
            if (randPipe != lastSpawnedPipe && !pipes[randPipe].active) {
                pipes[randPipe].setActive(true);
                lastSpawnedPipe = randPipe;
                break;
            }
        }
    }

    private Clip loadSound(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File not found: " + path);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip c = AudioSystem.getClip();
            c.open(audioStream);
            return c;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound: " + path);
            e.printStackTrace();
            return null;
        }
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

    public void restartGame() {
        // Reset bird position and velocity
        this.bird.setY(getHeight() / 2 - birdFrames[0].getHeight(null) / 2);
        this.bird.setVelocity(0);

        // Reset pipes
        for (Pipe pipe : pipes) {
            pipe.setX(getWidth());
            pipe.setActive(false);
            pipe.pointAwarded = false;
        }

        // Reset movement variables
        x1 = 0;
        x2 = getWidth();
        birdFrame = 0;
        lastSpawnedPipe = 0;
        lastAnimationTime = System.currentTimeMillis();
        lastPipeSpawnedTime = System.currentTimeMillis();
        pointCount = 0;
        game.points.setText(String.valueOf(pointCount));
        isRunning = true;

        timer.start();
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
                restartGame();
                bird.setVelocity(BIRD_JUMP_VELOCITY);
            }
            bird.setVelocity(BIRD_JUMP_VELOCITY);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
