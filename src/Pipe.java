import java.awt.*;

public class Pipe {
    int x, width, frameIndex, distance;
    int y1, y2, height1, height2;
    Rectangle upper, lower;
    boolean active;

    public Pipe(int x, int width, int frameIndex) {
        this.x = x;
        this.distance = 240;
        this.y1 = 0;

        switch (frameIndex) {
            case 0: { this.y2 = 120+distance; break; }
            case 1: { this.y2 = 180+distance; break; }
            case 2: { this.y2 = 240+distance; break; }
            case 3: { this.y2 = 330+distance; break; }
            case 4: { this.y2 = 400+distance; break; }
        }

        this.width = width;
        this.active = false;
        this.frameIndex = frameIndex;
    }

    public void moveLeft(int speed) { x -= speed; }

    public int getX() { return this.x; }
    public int getY1() { return this.y1; }
    public int getY2() { return this.y2; }
    public int getWidth() { return this.width; }
    public int getHeight1() { return this.height1; }
    public int getHeight2() { return this.height2; }
    public int getFrameIndex() { return this.frameIndex; }

    public void setY1(int y1) { this.y1 = y1; }
    public void setY2(int y2) { this.y2 = y2; }
    public void setHeight1(int height1) { this.height1 = height1; }
    public void setHeight2(int height2) { this.height2 = height2; }
    public void setX(int x) { this.x = x; }
    public void setActive(boolean value) { this.active = value; }
}
