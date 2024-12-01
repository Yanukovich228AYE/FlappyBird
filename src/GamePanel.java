import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.net.URL;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    Timer timer;
    private int  birdY, x1, x2; // x1, x2 used for animating the movement of the background and ground
    private int birdFrame, pipeFrame, lastSpawnedPipe;
    private long lastAnimationTime, lastPipeSpawnedTime; // saving last frame change time
    private int birdVelocity = 0;
    private boolean initialized = false; // solving the problem with background movement in the start of the game

    private final Image[] birdFrames; // bird frames for animation
    private final Image[] pipeFrames; // pipe images
    private final Pipe[] pipes; // pipe rectangle value holders
    private final Image bg1, bg2; // 2 equal backgrounds for movement animation
    private final Image ground1, ground2; // 2 equal grounds for movement animation

    public GamePanel() {
        timer = new Timer(20, this); // actionPerformed method will be responsible for updating panel every 20ms
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
        this.pipes = new Pipe[] {
                new Pipe(getWidth(), 0, pipeFrames[0].getWidth(null), getHeight(), 0),
                new Pipe(getWidth(), 0, pipeFrames[1].getWidth(null), getHeight(), 1),
                new Pipe(getWidth(), 0, pipeFrames[2].getWidth(null), getHeight(), 2),
                new Pipe(getWidth(), 0, pipeFrames[3].getWidth(null), getHeight(), 3),
                new Pipe(getWidth(), 0, pipeFrames[4].getWidth(null), getHeight(), 4)
        };
        this.ground1 = loadImg("resources\\Ground\\ground.png");
        this.ground2 = loadImg("resources\\Ground\\ground.png");
        this.bg1 = loadImg("resources\\Background\\bgLight.png");
        this.bg2 = loadImg("resources\\Background\\bgLight.png");

        setFocusable(true);
        addKeyListener(this);

        this.lastSpawnedPipe = 0;
        this.lastAnimationTime = System.currentTimeMillis();
        this.lastPipeSpawnedTime = System.currentTimeMillis();
        timer.start();
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

        if (!initialized) {
            this.x1 = 0;
            this.x2 = getWidth();
            this.birdY = getHeight()/2-birdFrames[0].getHeight(null)/2;
            this.birdFrame = 0;
            this.pipeFrame = 0;
            for (Pipe pipe : pipes) {
                pipe.setX(getWidth());
                pipe.setHeight(getHeight() - ground1.getHeight(null));
                pipe.setActive(false);
            }
            this.initialized = true;
        }

        // still background
        g.drawImage(bg1, 0, 0, getWidth(), getHeight(), null);

        // moving background
//        g.drawImage(bg1, x1, 0, getWidth(), getHeight(), null);
//        g.drawImage(bg2, x2, 0, getWidth(), getHeight(), null);

        // draw bird
        g.drawImage(birdFrames[birdFrame], getWidth()/2-birdFrames[birdFrame].getWidth(null)/2, birdY, birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null), null);
        g.drawRect(getWidth()/2-birdFrames[birdFrame].getWidth(null)/2, birdY, birdFrames[birdFrame].getWidth(null), birdFrames[birdFrame].getHeight(null));

        //g.drawImage(pipeFrames[pipeFrame], pipeX, 0, pipeFrames[pipeFrame].getWidth(null), getHeight(), null);
        for (Pipe pipe : pipes) {
            if (pipe.active) {
                g.drawImage(pipeFrames[pipe.getFrameIndex()], pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight(), null);
                g.drawRect(pipe.getX(), pipe.getY(), pipe.getWidth(), pipe.getHeight());
            }
        }

        g.drawImage(ground1, x1, getHeight()-ground1.getHeight(null), getWidth(), ground1.getHeight(null), null);
        g.drawImage(ground2, x2, getHeight()-ground2.getHeight(null), getWidth(), ground2.getHeight(null), null);
    }

    @Override
    public void actionPerformed(ActionEvent e) { // method that is responsible for updating the game periodically (updating frames)
        int gravity = 1;
        birdVelocity += gravity;
        birdY += birdVelocity;

        // updates every active pipe
        long gameVelocity = 4;
        for (Pipe pipe : pipes) {
            if (pipe.active) {
                pipe.moveLeft((int) gameVelocity);
                if (pipe.getX() <= -pipe.getWidth()) { // if pipe is out of the screen
                    pipe.setActive(false); // deactivate pipe
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
        if (currentTime - lastPipeSpawnedTime > (gameVelocity *1000- gameVelocity *350)) {
            int randPipe;
            while (true) {
                randPipe = (int) (Math.random() * pipes.length); // get random pipe
                if (randPipe != lastSpawnedPipe && !pipes[randPipe].active) { // check if the pipe is already spawned
                    pipes[randPipe].setActive(true);
                    lastSpawnedPipe = randPipe;
                    System.out.println("Pipe spawned: " + lastSpawnedPipe);
                    break;
                }
            }
            lastPipeSpawnedTime = currentTime;
        }

        if (birdY >= getHeight()-birdFrames[birdFrame].getHeight(null)-ground1.getHeight(null)) {
            birdY = getHeight() - birdFrames[birdFrame].getHeight(null) - ground1.getHeight(null);
            birdVelocity = 0;
        }

        repaint();
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
            birdVelocity = -10;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
