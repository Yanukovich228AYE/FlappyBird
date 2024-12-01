public class Pipe {
    int x, y, width, height, frameIndex;
    boolean active;

    public Pipe(int x, int y, int width, int height, int frameIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.active = false;
        this.frameIndex = frameIndex;
    }

    public void moveLeft(int speed) { x -= speed; }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getFrameIndex() { return this.frameIndex; }

    public void setX(int x) { this.x = x; }
    public void setHeight(int height) { this.height = height; }
    public void setActive(boolean value) { this.active = value; }
}
