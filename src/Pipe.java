import java.awt.*;

public class Pipe {
    int x, width, frameIndex, distance;
    double ratio;
    int y1, y2, height1, height2;
    boolean active, pointAwarded;

    public Pipe(int x, int width, int height, int frameIndex) {
        this.x = x;
        this.ratio = (double) height / 657.0;
        this.distance = (int) (192.0*ratio);
        this.width = width;
        this.y1 = 0;

        switch (frameIndex) {
            case 0: { this.height1 = (int) (104*ratio); break; }
            case 1: { this.height1 = (int) (172*ratio); break; }
            case 2: { this.height1 = (int) (232*ratio); break; }
            case 3: { this.height1 = (int) (292*ratio); break; }
            case 4: { this.height1 = (int) (360*ratio); break; }
        }

        this.y2 = this.height1 + distance;
        this.height2 = height - distance - height1;
        this.active = false;
        this.pointAwarded = false;
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
    public Rectangle getUpper() { return new Rectangle(this.x, this.y1, this.width, this.height1); }
    public Rectangle getLower() { return new Rectangle(this.x, this.y2, this.width, this.height2); }

    public void setY1(int y1) { this.y1 = y1; }
    public void setY2(int y2) { this.y2 = y2; }
    public void setHeight1(int height1) { this.height1 = height1; }
    public void setHeight2(int height2) { this.height2 = height2; }
    public void setX(int x) { this.x = x; }
    public void setActive(boolean value) { this.active = value; }
    public void setPointAwarded(boolean value) { this.pointAwarded = value; }
}
