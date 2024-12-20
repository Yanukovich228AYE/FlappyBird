import java.util.Random;

public class NeuralNetwork {
    private double[][] weightsIH; // Input-to-Hidden layer weights
    private double[][] weightsHO; // Hidden-to-Output layer weights
    private double[] biasH;
    private double[] biasO;

    private Random rand = new Random();

    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize) {
        weightsIH = new double[hiddenSize][inputSize];
        weightsHO = new double[outputSize][hiddenSize];
        biasH = new double[hiddenSize];
        biasO = new double[outputSize];

        randomizeWeights();
    }

    private void randomizeWeights() {
        // Initialize weights with small random values
        for (int i = 0; i < weightsIH.length; i++) {
            for (int j = 0; j < weightsIH[0].length; j++) {
                weightsIH[i][j] = rand.nextGaussian() * 0.5;
            }
            biasH[i] = rand.nextGaussian() * 0.5;
        }

        for (int i = 0; i < weightsHO.length; i++) {
            for (int j = 0; j < weightsHO[0].length; j++) {
                weightsHO[i][j] = rand.nextGaussian() * 0.5;
            }
        }
        for (int i = 0; i < biasO.length; i++) {
            biasO[i] = rand.nextGaussian() * 0.5;
        }
    }

    public double feedForward(double[] inputs) {
        // Calculate hidden layer
        double[] hidden = new double[weightsIH.length];
        for (int i = 0; i < weightsIH.length; i++) {
            double sum = 0;
            for (int j = 0; j < weightsIH[0].length; j++) {
                sum += inputs[j] * weightsIH[i][j];
            }
            sum += biasH[i];
            hidden[i] = sigmoid(sum);
        }

        // Calculate output layer
        double[] output = new double[weightsHO.length];
        for (int i = 0; i < weightsHO.length; i++) {
            double sum = 0;
            for (int j = 0; j < weightsHO[0].length; j++) {
                sum += hidden[j] * weightsHO[i][j];
            }
            sum += biasO[i];
            output[i] = sigmoid(sum);
        }

        // Since we have only one output neuron, return it
        return output[0];
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
}
