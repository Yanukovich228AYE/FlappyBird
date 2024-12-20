public class Bird {
    int x, y, velocity;
    int width, height;
    NeuralNetwork brain;

    public Bird(int windowHeight, int width, int height, int y, int x) {
        this.width = width;
        this.height = height;
        this.y = y;
        this.x = x;
        this.velocity = 0;

        this.brain = new NeuralNetwork(4); // 4 input variables
    }

    public boolean shouldFlap(double birdY, double birdVelocity, double closestPipeDist, double pipeGapY) {
        double inputs[] = {birdY, birdVelocity, closestPipeDist, pipeGapY}; // insert inputs of the bird
        double output = brain.feed(inputs); // feed inputs
        return output > 0.5;
    }

    public int getY() { return this.y; }
    public int getX() { return this.x; }
    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }
    public int getVelocity() { return this.velocity; }

    public void setY(int y) { this.y = y; }
    public void setX(int x) { this.x = x; }
    public void setVelocity(int velocity) { this.velocity = velocity; }
}
