public class Bird {
    int y, velocity;
    int width, height;

    public Bird(int windowHeight, int width, int height) {
        this.width = width;
        this.height = height;
        this.y = windowHeight/2-height/2;
        this.velocity = 0;
    }

    public int getY() { return this.y; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getVelocity() { return this.velocity; }
}
