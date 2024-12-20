public class NeuralNetwork {
    double[][] weightsIH;
    double[][] weightsHO;

    private double bias;

    public NeuralNetwork(int numWeights) {
        this.weights = new double[numWeights];
        randomizeWeights();
    }

    public void randomizeWeights() {
        for (int i = 0; i < weights.length; i++)
            weights[i] = Math.random() * 2 - 1;

        bias = Math.random() * 2 - 1;
    }

    public double feed(double[] inputs) {
        double sum = 0;
        for (int i = 0; i < inputs.length; i++) {
            sum += inputs[i] * weights[i];
        }

        sum += bias;
        return sigmoid(sum);
    }

    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] newWeights) {
        this.weights = newWeights.clone();
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double b) {
        this.bias = b;
    }
}
