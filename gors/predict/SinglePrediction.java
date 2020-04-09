package gors.predict;

public class SinglePrediction {
    char predictedState;
    private double propH;
    private double probC;
    private double probE;

    /**
     * constructs a prediction object
     * @param predictedState character of the predicted structure: H,C or E
     * @param propH probability for structure H for this prediction
     * @param probC probability for structure C for this prediction
     * @param probE probability for structure E for this prediction
     */
    public SinglePrediction(char predictedState, double propH, double probC, double probE) {
        this.predictedState = predictedState;
        this.propH = propH;
        this.probC = probC;
        this.probE = probE;
    }

    public char getPredictedState() {
        return predictedState;
    }

    public void setPredictedState(char predictedState) {
        this.predictedState = predictedState;
    }

    public double getPropH() {
        return propH;
    }

    public void setPropH(double propH) {
        this.propH = propH;
    }

    public double getProbC() {
        return probC;
    }

    public void setProbC(double probC) {
        this.probC = probC;
    }

    public double getProbE() {
        return probE;
    }

    public void setProbE(double probE) {
        this.probE = probE;
    }
}
