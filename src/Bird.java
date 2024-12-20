public class Bird {
    int x, y, velocity;
    int width, height;
    NeuralNetwork brain; // The bird's "brain"

    public Bird(int windowHeight, int width, int height, int y, int x) {
        this.width = width;
        this.height = height;
        this.y = y;
        this.x = x;
        this.velocity = 0;
        // Initialize the brain with the desired architecture (e.g. 4 inputs, 5 hidden, 1 output)
        this.brain = new NeuralNetwork(4, 5, 1);
    }

    // Decide whether to flap or not, based on inputs
    public boolean shouldFlap(double birdY, double birdVel, double nextPipeDist, double pipeGapY) {
        double[] inputs = {birdY, birdVel, nextPipeDist, pipeGapY};
        double output = brain.feedForward(inputs);
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
