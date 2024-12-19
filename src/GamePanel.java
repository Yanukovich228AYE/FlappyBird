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
    Game game;
    Timer timer;
    private int x1, x2; // x1, x2 used for animating the movement of the background and ground
    private int birdFrame, pipeFrame, lastSpawnedPipe;
    private long lastAnimationTime, lastPipeSpawnedTime; // saving last frame change time
    private int pointCount = 0;
    private boolean isRunning = false; // solving the problem with background movement in the start of the game

    private Bird bird;
    private final Image[] birdFrames; // bird frames for animation
    private final Image[] pipeFrames; // pipe images
    private final Pipe[] pipes; // pipe rectangle value holders
    private Clip wingClip;
    private final Image bg1; // 2 equal backgrounds for movement animation
    private final Image ground1, ground2; // 2 equal grounds for movement animation

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
        loadSound(wingClip, "src\\resources\\Audio\\sfx_wing.wav");
        this.ground1 = loadImg("resources\\Ground\\ground.png");
        this.ground2 = loadImg("resources\\Ground\\ground.png");
        this.bg1 = loadImg("resources\\Background\\bgLight.png");

        setFocusable(true);
        addKeyListener(this);

        bird = new Bird(game.getHeight(), birdFrames[0].getWidth(null), birdFrames[0].getHeight(null), game.getHeight()/2-birdFrames[0].getHeight(null)/2, game.getWidth()/2-birdFrames[0].getWidth(null)/2);
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
//    public Pipe getClosestPipe() {
//        int closestY;
//        for (Pipe pipe : pipes) {
//            int temp = pipe.getY1();
//            if ()
//        }
//    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // still background
        g.drawImage(bg1, 0, 0, getWidth(), getHeight(), null);

        // moving background
//        g.drawImage(bg1, x1, 0, getWidth(), getHeight(), null);
//        g.drawImage(bg2, x2, 0, getWidth(), getHeight(), null);

        // draw bird
        Rectangle birdRect = new Rectangle(bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight());
        g.drawImage(birdFrames[birdFrame], bird.getX(), bird.getY(), birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null), null);
        //g.drawRect(getWidth()/2-birdFrames[birdFrame].getWidth(null)/2, bird.getY(), birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null));



        for (Pipe pipe : pipes) {
            if (pipe.active) {
                g.drawImage(pipeFrames[pipe.getFrameIndex()], pipe.getX(), pipe.getY1(), pipe.getWidth(), getHeight(), null);
                //g.drawRect(pipe.getX(), pipe.getY1(), pipe.getWidth(), pipe.getHeight1()); // rect 1
                //g.drawRect(pipe.getX(), pipe.getY2(), pipe.getWidth(), pipe.getHeight2()); // rect 2
                if (birdRect.intersects(pipe.getUpper()) || birdRect.intersects(pipe.getLower())) { // check bird intersection with pipe
                    isRunning = false;
                    timer.stop();
                } else if (birdRect.intersects(pipe.getX()+pipe.getWidth(), pipe.getHeight1(), pipe.getX()+pipe.getWidth(), pipe.getY2()) && !pipe.pointAwarded) { // if intersects the line right after the pipe +1 point
                    game.points.setText("" + ++pointCount);
                    pipe.pointAwarded = true;
                }
            }
        }

        g.drawImage(ground1, x1, getHeight()-ground1.getHeight(null), getWidth(), ground1.getHeight(null), null);
        g.drawImage(ground2, x2, getHeight()-ground2.getHeight(null), getWidth(), ground2.getHeight(null), null);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // method that is responsible for updating the game periodically (updating frames)
        int gravity = 1;

        if (isRunning) {
            bird.setVelocity(bird.getVelocity()+gravity);
            bird.setY(bird.getY()+bird.getVelocity());
        }

        // updates every active pipe
        long gameVelocity = 4;
        for (Pipe pipe : pipes) {
            if (pipe.active) {
                pipe.moveLeft((int) gameVelocity);
                if (pipe.getX() <= -pipe.getWidth()) { // if pipe is out of the screen
                    pipe.setActive(false); // deactivate pipe
                    pipe.pointAwarded = false;
                    pipe.setX(getWidth()); // reset position
                    System.out.println("Pipe deactivated: " + pipe);
                }
            }
        }

        x1 -= (int)gameVelocity;
        x2 -= (int)gameVelocity;

        if (x1 <= -getWidth()) // controlling background and ground movement
            x1 = x2 + getWidth();
        if (x2 <= -getWidth())
            x2 = x1 + getWidth();

        if (pipes[pipeFrame].getX() <= -pipes[pipeFrame].getWidth()) {
            pipes[pipeFrame].setX(getWidth());
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAnimationTime > 100) { // changes bird frame every specified interval of time
            if (birdFrame >= 2)
                birdFrame = 0;
            else
                birdFrame++;
            lastAnimationTime = currentTime;
        }

        // Spawn new random pipe every specified interval of time
        if (currentTime - lastPipeSpawnedTime > (gameVelocity *1000- gameVelocity *350) && isRunning) {
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

        if (bird.getY() >= getHeight()-birdFrames[birdFrame].getHeight(null)-ground1.getHeight(null)) {
            bird.setY(getHeight() - birdFrames[birdFrame].getHeight(null) - ground1.getHeight(null));
            bird.setVelocity(0);
            isRunning = false;
            timer.stop();
        }

        // prevent bird from going out of the top of the screen\
        if (bird.getY() <= 0) {
            bird.setY(0);
            bird.setVelocity(0);
        }

        repaint();
    }

    public void playSound(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File not found: " + path);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + path);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + path);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + path);
            e.printStackTrace();
        }
    }

    private void loadSound(Clip clip, String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("File not found: " + path);
                return;
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);

            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(path)));
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format: " + path);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + path);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + path);
            e.printStackTrace();
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
        }

        // Reset movement variables
        this.x1 = 0;
        this.x2 = getWidth();
        this.birdFrame = 0;
        this.pipeFrame = 0;
        this.lastSpawnedPipe = 0;
        this.lastAnimationTime = System.currentTimeMillis();
        this.lastPipeSpawnedTime = System.currentTimeMillis();

        // Re-initialize game state
        pointCount = 0;
        game.points.setText("" + pointCount);
        this.isRunning = true;

        // Start the timer again
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
            }
            bird.setVelocity(-15);

            if (wingClip != null) {
                if (wingClip.isRunning()) {
                    wingClip.stop();
                }
                wingClip.setFramePosition(0);
                wingClip.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}