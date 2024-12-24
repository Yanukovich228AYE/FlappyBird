public class Bird {
    int x, y, height, width;
    public int velocity;

    public Bird(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.velocity = 0;
    }

    public int getY() { return this.y; }
    public int getX() { return this.x; }
    public int getVelocity() { return this.velocity; }

    public void setY(int y) { this.y = y; }
    public void setX(int x) { this.x = x; }
    public void setHeight(int height) { this.height = height; }
    public void setWidth(int width) { this.width = width; }
    public void setVelocity(int velocity) { this.velocity = velocity; }
}
